package com.primus.server.export;

import com.primus.common.constant.PrimusConstants;
import com.primus.common.exception.NotFoundException;
import com.primus.common.exception.ValidationException;
import com.primus.contract.request.ExportRequest;
import com.primus.contract.response.ExportStatusResponse;
import com.primus.metadata.model.ApplicationMetadata;
import com.primus.metadata.model.FieldMetadata;
import com.primus.metadata.registry.MetadataRegistry;
import com.primus.server.audit.AuditService;
import com.primus.server.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core export service. Executes metadata-driven export operations and persists
 * the packaged payload via the {@link StorageProvider}.
 *
 * <p>Tier 1 scope: synchronous execution with retry bounds and status lifecycle.
 */
@Service
public class ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    private final MetadataRegistry metadataRegistry;
    private final StorageProvider storage;
    private final AuditService auditService;

    /** In-memory export status store. Replace with DB persistence in Tier 2+. */
    private final Map<String, ExportRecord> exportStore = new ConcurrentHashMap<>();

    public ExportService(MetadataRegistry metadataRegistry, StorageProvider storage,
                         AuditService auditService) {
        this.metadataRegistry = metadataRegistry;
        this.storage = storage;
        this.auditService = auditService;
    }

    /**
     * Initiate an export and execute it synchronously (async in later tiers).
     *
     * @return the export status after execution
     */
    public ExportStatusResponse initiateExport(ExportRequest request) {
        validateRequest(request);

        String exportId = UUID.randomUUID().toString();
        ApplicationMetadata meta = metadataRegistry.get(request.getAppId());

        ExportRecord record = new ExportRecord(exportId, request.getAppId(),
                request.getTransactionId(), request.getRequestedBy(),
                request.getCorrelationId());
        exportStore.put(exportId, record);

        auditService.recordEvent("EXPORT_REQUESTED", request.getRequestedBy(),
                request.getAppId(), exportId, request.getCorrelationId());

        record.setStatus(PrimusConstants.EXPORT_STATUS_RUNNING);
        int attempt = 0;
        while (attempt < PrimusConstants.MAX_EXPORT_RETRY_ATTEMPTS) {
            try {
                byte[] payload = buildPayload(meta, request);
                String format = determineFormat(request.getOutputFormat());
                String checksum = storage.put(request.getAppId(), exportId, format, payload);
                record.setStatus(PrimusConstants.EXPORT_STATUS_COMPLETED);
                record.setCompletedAt(Instant.now());
                record.setChecksum(checksum);
                record.setFormat(format);
                auditService.recordEvent("EXPORT_COMPLETED", request.getRequestedBy(),
                        request.getAppId(), exportId, request.getCorrelationId());
                log.info("Export completed exportId={} appId={} bytes={}",
                        exportId, request.getAppId(), payload.length);
                break;
            } catch (Exception e) {
                attempt++;
                log.warn("Export attempt {}/{} failed for exportId={}: {}",
                        attempt, PrimusConstants.MAX_EXPORT_RETRY_ATTEMPTS, exportId, e.getMessage());
                if (attempt >= PrimusConstants.MAX_EXPORT_RETRY_ATTEMPTS) {
                    record.setStatus(PrimusConstants.EXPORT_STATUS_FAILED);
                    record.setFailureReason(e.getMessage());
                    auditService.recordEvent("EXPORT_FAILED", request.getRequestedBy(),
                            request.getAppId(), exportId, request.getCorrelationId());
                }
            }
        }

        return toStatusResponse(record);
    }

    public ExportStatusResponse getStatus(String exportId) {
        ExportRecord record = requireRecord(exportId);
        return toStatusResponse(record);
    }

    /**
     * Cancel a running export. Only valid while status is REQUESTED or RUNNING.
     */
    public void cancel(String exportId, String requestedBy) {
        ExportRecord record = requireRecord(exportId);
        if (PrimusConstants.EXPORT_STATUS_COMPLETED.equals(record.getStatus())
                || PrimusConstants.EXPORT_STATUS_FAILED.equals(record.getStatus())) {
            throw new ValidationException("Cannot cancel an export in status: " + record.getStatus());
        }
        record.setStatus(PrimusConstants.EXPORT_STATUS_CANCELLED);
        auditService.recordEvent("EXPORT_CANCELLED", requestedBy,
                record.getAppId(), exportId, record.getCorrelationId());
        log.info("Export cancelled exportId={} by={}", exportId, requestedBy);
    }

    public ExportRecordView getRecordView(String exportId) {
        return new ExportRecordView(requireRecord(exportId));
    }

    // -------------------------------------------------------------------------

    private void validateRequest(ExportRequest request) {
        if (request == null) throw new ValidationException("Export request must not be null");
        if (request.getAppId() == null || request.getAppId().isBlank())
            throw new ValidationException("appId is required");
        if (request.getTransactionId() == null || request.getTransactionId().isBlank())
            throw new ValidationException("transactionId is required");
        if (request.getRequestedBy() == null || request.getRequestedBy().isBlank())
            throw new ValidationException("requestedBy is required");
    }

    private byte[] buildPayload(ApplicationMetadata meta, ExportRequest request) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"transactionId\":\"").append(request.getTransactionId()).append("\"");
        json.append(",\"appId\":\"").append(meta.getAppId()).append("\"");
        json.append(",\"fields\":{");
        boolean first = true;
        for (FieldMetadata field : meta.getFields()) {
            if (!first) json.append(",");
            String value = request.getParameters() != null
                    ? request.getParameters().getOrDefault(field.getName(), "")
                    : "";
            if (field.isSensitive()) {
                value = maskValue(value, field);
            }
            json.append("\"").append(field.getName()).append("\":\"").append(value).append("\"");
            first = false;
        }
        json.append("}}");
        return json.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String maskValue(String value, FieldMetadata field) {
        if (value == null || value.isBlank()) return value;
        return switch (field.getMaskStrategy()) {
            case "LAST_N" -> {
                int visible = field.getVisibleChars();
                int len = value.length();
                if (visible >= len) yield "*".repeat(len);
                yield "*".repeat(len - visible) + value.substring(len - visible);
            }
            case "REDACT" -> "[REDACTED]";
            default -> "*".repeat(value.length());
        };
    }

    private String determineFormat(String requested) {
        if ("XML".equalsIgnoreCase(requested)) return "xml";
        return "json";
    }

    private ExportRecord requireRecord(String exportId) {
        ExportRecord record = exportStore.get(exportId);
        if (record == null) throw new NotFoundException("Export", exportId);
        return record;
    }

    private ExportStatusResponse toStatusResponse(ExportRecord r) {
        ExportStatusResponse resp = new ExportStatusResponse();
        resp.setExportId(r.getExportId());
        resp.setAppId(r.getAppId());
        resp.setTransactionId(r.getTransactionId());
        resp.setStatus(r.getStatus());
        resp.setRequestedBy(r.getRequestedBy());
        resp.setRequestedAt(r.getRequestedAt());
        resp.setCompletedAt(r.getCompletedAt());
        resp.setFailureReason(r.getFailureReason());
        resp.setCorrelationId(r.getCorrelationId());
        resp.setRetryCount(r.getRetryCount());
        return resp;
    }
}
