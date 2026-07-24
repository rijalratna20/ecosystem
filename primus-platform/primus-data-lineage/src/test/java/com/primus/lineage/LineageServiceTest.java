package com.primus.lineage;

import com.primus.lineage.domain.LineageEdge;
import com.primus.lineage.domain.LineageNode;
import com.primus.lineage.domain.NodeType;
import com.primus.lineage.service.InMemoryLineageService;
import com.primus.lineage.service.LineageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LineageServiceTest {

    private LineageService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryLineageService();
    }

    @Test
    void record_and_find_node() {
        LineageNode node = new LineageNode("n1", NodeType.SOURCE, "CustomerDB", "app-1", Map.of("db", "postgres"));
        service.recordNode(node);
        assertNotNull(service.findNode("n1"));
        assertEquals("CustomerDB", service.findNode("n1").getLabel());
    }

    @Test
    void record_edge_and_traverse() {
        service.recordNode(new LineageNode("n1", NodeType.SOURCE, "DB", "app-1", null));
        service.recordNode(new LineageNode("n2", NodeType.EXPORT, "Export-20240101", "app-1", null));
        service.recordEdge(new LineageEdge("e1", "n1", "n2", "EXPORTED_TO"));

        List<LineageEdge> downstream = service.findDownstreamEdges("n1");
        assertEquals(1, downstream.size());
        assertEquals("n2", downstream.get(0).getToNodeId());

        List<LineageEdge> upstream = service.findUpstreamEdges("n2");
        assertEquals(1, upstream.size());
        assertEquals("n1", upstream.get(0).getFromNodeId());
    }

    @Test
    void find_by_application_and_type() {
        service.recordNode(new LineageNode("n3", NodeType.EXPORT, "E1", "app-A", null));
        service.recordNode(new LineageNode("n4", NodeType.EXPORT, "E2", "app-A", null));
        service.recordNode(new LineageNode("n5", NodeType.SOURCE, "DB", "app-A", null));

        List<LineageNode> exports = service.findByApplicationAndType("app-A", NodeType.EXPORT);
        assertEquals(2, exports.size());
    }

    @Test
    void search_by_attribute() {
        service.recordNode(new LineageNode("n6", NodeType.EXPORT, "CustExport", "app-1",
                Map.of("customerId", "12345", "field", "ssn")));
        service.recordNode(new LineageNode("n7", NodeType.EXPORT, "OtherExport", "app-1",
                Map.of("customerId", "99999")));

        List<LineageNode> results = service.searchByAttribute("customerId", "12345");
        assertEquals(1, results.size());
        assertEquals("n6", results.get(0).getId());
    }

    @Test
    void record_null_node_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.recordNode(null));
    }
}
