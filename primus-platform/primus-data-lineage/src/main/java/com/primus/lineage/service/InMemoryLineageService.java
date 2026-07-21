package com.primus.lineage.service;

import com.primus.lineage.domain.LineageEdge;
import com.primus.lineage.domain.LineageNode;
import com.primus.lineage.domain.NodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** In-memory implementation of {@link LineageService}. */
public class InMemoryLineageService implements LineageService {

    private final Map<String, LineageNode> nodes = new ConcurrentHashMap<>();
    private final List<LineageEdge> edges = new ArrayList<>();

    @Override
    public LineageNode recordNode(LineageNode node) {
        if (node == null) throw new IllegalArgumentException("node must not be null");
        nodes.put(node.getId(), node);
        return node;
    }

    @Override
    public synchronized LineageEdge recordEdge(LineageEdge edge) {
        if (edge == null) throw new IllegalArgumentException("edge must not be null");
        edges.add(edge);
        return edge;
    }

    @Override
    public LineageNode findNode(String nodeId) {
        return nodes.get(nodeId);
    }

    @Override
    public List<LineageNode> findByApplicationAndType(String applicationId, NodeType type) {
        return nodes.values().stream()
                .filter(n -> type == n.getType() && applicationId.equals(n.getApplicationId()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<LineageEdge> findDownstreamEdges(String nodeId) {
        return edges.stream().filter(e -> nodeId.equals(e.getFromNodeId())).collect(Collectors.toList());
    }

    @Override
    public synchronized List<LineageEdge> findUpstreamEdges(String nodeId) {
        return edges.stream().filter(e -> nodeId.equals(e.getToNodeId())).collect(Collectors.toList());
    }

    @Override
    public List<LineageNode> searchByAttribute(String attributeKey, String attributeValue) {
        return nodes.values().stream()
                .filter(n -> attributeValue.equals(n.getAttributes().get(attributeKey)))
                .collect(Collectors.toList());
    }
}
