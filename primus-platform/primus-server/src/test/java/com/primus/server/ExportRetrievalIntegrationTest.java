package com.primus.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primus.contract.request.ApplicationRegistrationRequest;
import com.primus.contract.request.ExportRequest;
import com.primus.metadata.model.ApplicationMetadata;
import com.primus.metadata.registry.MetadataRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the core export/retrieval flow (Tier 1 exit criteria).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExportRetrievalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MetadataRegistry metadataRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void registerApp() {
        // Register a minimal app for test
        if (!metadataRegistry.contains("test-app")) {
            ApplicationMetadata meta = new ApplicationMetadata();
            meta.setAppId("test-app");
            meta.setDisplayName("Test Application");
            metadataRegistry.register(meta);
        }
    }

    @Test
    void registerApplication_success() throws Exception {
        ApplicationRegistrationRequest req = new ApplicationRegistrationRequest();
        req.setAppId("new-app");
        req.setDisplayName("New App");

        mockMvc.perform(post("/api/v1/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void initiateExport_success() throws Exception {
        ExportRequest req = new ExportRequest();
        req.setAppId("test-app");
        req.setTransactionId("txn-001");
        req.setRequestedBy("user1");
        req.setOutputFormat("json");
        req.setParameters(Map.of("field1", "value1"));

        mockMvc.perform(post("/api/v1/exports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.appId").value("test-app"))
                .andExpect(jsonPath("$.data.transactionId").value("txn-001"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void initiateExport_missingAppId_returns400() throws Exception {
        ExportRequest req = new ExportRequest();
        req.setTransactionId("txn-002");
        req.setRequestedBy("user1");

        mockMvc.perform(post("/api/v1/exports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    void getExportStatus_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/exports/nonexistent/status"))
                .andExpect(status().isNotFound());
    }
}
