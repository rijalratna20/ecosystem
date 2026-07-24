package com.primus.metadata.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Metadata definition for a registered Primus application, including all
 * fields and their export/masking configuration.
 */
public class ApplicationMetadata {

    private String appId;
    private String displayName;
    private String version;
    private String namespace;
    private String rootEntityClass;
    private List<FieldMetadata> fields = new ArrayList<>();

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public String getRootEntityClass() { return rootEntityClass; }
    public void setRootEntityClass(String rootEntityClass) { this.rootEntityClass = rootEntityClass; }

    public List<FieldMetadata> getFields() { return Collections.unmodifiableList(fields); }

    public void addField(FieldMetadata field) {
        this.fields.add(field);
    }

    public void setFields(List<FieldMetadata> fields) {
        this.fields = fields == null ? new ArrayList<>() : new ArrayList<>(fields);
    }
}
