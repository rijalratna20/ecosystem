package com.primus.lineage.domain;

import java.time.Instant;
import java.util.Map;

/** A node in the data-lineage graph (data source, export, retrieval, storage, etc.). */
public final class LineageNode {

    private final String id;
    private final NodeType type;
    private final String label;
    private final String applicationId;
    private final Instant recordedAt;
    private final Map<String, String> attributes;

    public LineageNode(String id, NodeType type, String label, String applicationId,
                       Map<String, String> attributes) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.applicationId = applicationId;
        this.recordedAt = Instant.now();
        this.attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public String getId() { return id; }
    public NodeType getType() { return type; }
    public String getLabel() { return label; }
    public String getApplicationId() { return applicationId; }
    public Instant getRecordedAt() { return recordedAt; }
    public Map<String, String> getAttributes() { return attributes; }

    @Override
    public String toString() {
        return "LineageNode{id='" + id + "', type=" + type + ", label='" + label + "'}";
    }
}
