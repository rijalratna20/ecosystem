package com.primus.server.retrieval;

import com.primus.common.exception.NotFoundException;
import com.primus.common.exception.ValidationException;
import com.primus.contract.request.RetrievalRequest;
import com.primus.contract.response.RetrievalResponse;
import com.primus.server.audit.AuditService;
import com.primus.server.export.ExportRecordView;
import com.primus.server.export.ExportService;
import com.primus.server.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Retrieves completed export artifacts and assembles the retrieval response.
 */
@Service
public class RetrievalService {

    private static final Logger log = LoggerFactory.getLogger(RetrievalService.class);

    private final ExportService exportService;
    private final StorageProvider storage;
    private final AuditService auditService;

    public RetrievalService(ExportService exportService, StorageProvider storage,
                            AuditService auditService) {
        this.exportService = exportService;
        this.storage = storage;
        this.auditService = auditService;
    }

    public RetrievalResponse retrieve(RetrievalRequest request) {
        if (request == null || request.getExportId() == null || request.getExportId().isBlank()) {
            throw new ValidationException("exportId is required");
        }

        auditService.recordEvent("RETRIEVAL_REQUESTED", request.getRequestedBy(),
                request.getAppId(), request.getExportId(), request.getCorrelationId());

        ExportRecordView record = exportService.getRecordView(request.getExportId());

        if (!"COMPLETED".equals(record.getStatus())) {
            throw new ValidationException(
                    "Export is not yet available. Current status: " + record.getStatus(),
                    "EXPORT_NOT_READY");
        }

        byte[] content = storage.get(record.getAppId(), record.getExportId(), record.getFormat());

        auditService.recordEvent("RETRIEVAL_COMPLETED", request.getRequestedBy(),
                request.getAppId(), request.getExportId(), request.getCorrelationId());

        log.info("Retrieval completed exportId={} by={} bytes={}",
                record.getExportId(), request.getRequestedBy(), content.length);

        RetrievalResponse response = new RetrievalResponse();
        response.setExportId(record.getExportId());
        response.setAppId(record.getAppId());
        response.setTransactionId(record.getTransactionId());
        response.setFormat(record.getFormat());
        response.setContent(content);
        response.setChecksum(record.getChecksum());
        response.setSizeBytes(content.length);
        response.setExportedAt(record.getCompletedAt());
        response.setCorrelationId(request.getCorrelationId());
        return response;
    }
}
