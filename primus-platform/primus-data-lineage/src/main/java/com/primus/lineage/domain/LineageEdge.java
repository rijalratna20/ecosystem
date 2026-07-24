package com.primus.lineage.domain;

import java.time.Instant;

/** A directed edge in the lineage graph connecting two {@link LineageNode}s. */
public final class LineageEdge {

    private final String id;
    private final String fromNodeId;
    private final String toNodeId;
    private final String relationshipType;
    private final Instant recordedAt;

    public LineageEdge(String id, String fromNodeId, String toNodeId, String relationshipType) {
        this.id = id;
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.relationshipType = relationshipType;
        this.recordedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getFromNodeId() { return fromNodeId; }
    public String getToNodeId() { return toNodeId; }
    public String getRelationshipType() { return relationshipType; }
    public Instant getRecordedAt() { return recordedAt; }

    @Override
    public String toString() {
        return "LineageEdge{" + fromNodeId + " --[" + relationshipType + "]--> " + toNodeId + '}';
    }
}
