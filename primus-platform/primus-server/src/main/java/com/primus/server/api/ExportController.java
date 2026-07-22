package com.primus.server.api;

import com.primus.common.dto.ApiResponse;
import com.primus.contract.request.ExportRequest;
import com.primus.contract.response.ExportStatusResponse;
import com.primus.server.export.ExportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for export operations.
 */
@RestController
@RequestMapping("/api/v1/exports")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /** Initiate a new export. */
    @PostMapping
    public ResponseEntity<ApiResponse<ExportStatusResponse>> createExport(
            @RequestBody ExportRequest request) {
        ExportStatusResponse status = exportService.initiateExport(request);
        return ResponseEntity.accepted().body(ApiResponse.ok(status));
    }

    /** Get export status. */
    @GetMapping("/{exportId}/status")
    public ResponseEntity<ApiResponse<ExportStatusResponse>> getStatus(
            @PathVariable String exportId) {
        ExportStatusResponse status = exportService.getStatus(exportId);
        return ResponseEntity.ok(ApiResponse.ok(status));
    }

    /** Cancel a running export. */
    @PostMapping("/{exportId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable String exportId,
            @RequestParam(defaultValue = "system") String requestedBy) {
        exportService.cancel(exportId, requestedBy);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
