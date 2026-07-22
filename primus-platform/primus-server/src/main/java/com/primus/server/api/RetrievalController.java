package com.primus.server.api;

import com.primus.common.dto.ApiResponse;
import com.primus.contract.request.RetrievalRequest;
import com.primus.contract.response.RetrievalResponse;
import com.primus.server.retrieval.RetrievalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for retrieval operations.
 */
@RestController
@RequestMapping("/api/v1/retrievals")
public class RetrievalController {

    private final RetrievalService retrievalService;

    public RetrievalController(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    /** Retrieve a completed export. */
    @PostMapping
    public ResponseEntity<ApiResponse<RetrievalResponse>> retrieve(
            @RequestBody RetrievalRequest request) {
        RetrievalResponse response = retrievalService.retrieve(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
