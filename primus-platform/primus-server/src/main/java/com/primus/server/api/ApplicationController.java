package com.primus.server.api;

import com.primus.common.dto.ApiResponse;
import com.primus.contract.request.ApplicationRegistrationRequest;
import com.primus.metadata.model.ApplicationMetadata;
import com.primus.metadata.parser.YamlMetadataParser;
import com.primus.metadata.registry.MetadataRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * REST controller for application registration and metadata management.
 */
@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final MetadataRegistry metadataRegistry;
    private final YamlMetadataParser yamlParser = new YamlMetadataParser();

    public ApplicationController(MetadataRegistry metadataRegistry) {
        this.metadataRegistry = metadataRegistry;
    }

    /** Register or update an application with its metadata contract. */
    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationMetadata>> register(
            @RequestBody ApplicationRegistrationRequest request) {
        ApplicationMetadata meta;
        if (request.getMetadataYaml() != null && !request.getMetadataYaml().isBlank()) {
            meta = yamlParser.parse(request.getMetadataYaml());
        } else {
            meta = new ApplicationMetadata();
            meta.setAppId(request.getAppId());
            meta.setDisplayName(request.getDisplayName());
        }
        metadataRegistry.register(meta);
        return ResponseEntity.ok(ApiResponse.created(meta));
    }

    /** List all registered applications. */
    @GetMapping
    public ResponseEntity<ApiResponse<Collection<ApplicationMetadata>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(metadataRegistry.all()));
    }

    /** Get metadata for a specific application. */
    @GetMapping("/{appId}")
    public ResponseEntity<ApiResponse<ApplicationMetadata>> get(@PathVariable String appId) {
        return ResponseEntity.ok(ApiResponse.ok(metadataRegistry.get(appId)));
    }

    /** Deregister an application. */
    @DeleteMapping("/{appId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String appId) {
        metadataRegistry.deregister(appId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
