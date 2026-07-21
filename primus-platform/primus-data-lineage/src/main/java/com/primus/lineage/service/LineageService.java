package com.primus.lineage.service;

import com.primus.lineage.domain.LineageEdge;
import com.primus.lineage.domain.LineageNode;
import com.primus.lineage.domain.NodeType;

import java.util.List;

/**
 * Service for recording and querying the data lineage graph.
 *
 * <p>Supports compliance investigations such as:
 * <ul>
 *   <li>"Show me all exports of customer 12345's SSN"</li>
 *   <li>"Who has accessed credit card data in the last 30 days?"</li>
 *   <li>"Where is this customer's data stored?"</li>
 * </ul>
 *
 * <p>REST mapping:
 * <pre>
 *   POST /api/v1/lineage/nodes           – record a lineage node
 *   POST /api/v1/lineage/edges           – record a lineage edge
 *   GET  /api/v1/lineage/nodes/{id}      – get a node
 *   GET  /api/v1/lineage/graph/{nodeId}  – get the full sub-graph for a node
 *   GET  /api/v1/lineage/search          – search by attribute (e.g. customerId)
 * </pre>
 */
public interface LineageService {

    /** Record a new lineage node. */
    LineageNode recordNode(LineageNode node);

    /** Record a directed edge between two nodes. */
    LineageEdge recordEdge(LineageEdge edge);

    /** Retrieve a node by id. */
    LineageNode findNode(String nodeId);

    /** Find all nodes of a given type for an application. */
    List<LineageNode> findByApplicationAndType(String applicationId, NodeType type);

    /**
     * Find all edges originating from a given node (downstream traversal).
     *
     * @param nodeId the starting node
     * @return edges leaving nodeId
     */
    List<LineageEdge> findDownstreamEdges(String nodeId);

    /**
     * Find all edges pointing to a given node (upstream traversal).
     *
     * @param nodeId the target node
     * @return edges arriving at nodeId
     */
    List<LineageEdge> findUpstreamEdges(String nodeId);

    /**
     * Search for nodes whose attributes contain the given key/value pair.
     * Useful for tracing specific data subjects (e.g. customerId=12345).
     *
     * @param attributeKey   the attribute to match
     * @param attributeValue the value to match
     * @return matching nodes
     */
    List<LineageNode> searchByAttribute(String attributeKey, String attributeValue);
}
