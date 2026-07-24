package com.primus.metadata;

import com.primus.annotations.PrimusEntity;
import com.primus.annotations.PrimusField;
import com.primus.annotations.PrimusId;
import com.primus.annotations.Sensitive;
import com.primus.metadata.model.ApplicationMetadata;
import com.primus.metadata.model.FieldMetadata;
import com.primus.metadata.parser.AnnotationMetadataParser;
import com.primus.metadata.parser.YamlMetadataParser;
import com.primus.metadata.registry.MetadataRegistry;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MetadataTest {

    // --- sample entity for annotation tests ---

    @PrimusEntity(name = "Order", namespace = "com.example")
    static class OrderEntity {
        @PrimusId
        private String orderId;

        @PrimusField(required = true, description = "Customer name")
        private String customerName;

        @PrimusField
        @Sensitive(strategy = Sensitive.MaskStrategy.LAST_N, visibleChars = 4)
        private String cardNumber;
    }

    @Test
    void annotationParser_parsesFields() {
        AnnotationMetadataParser parser = new AnnotationMetadataParser();
        ApplicationMetadata meta = parser.parse(OrderEntity.class);

        assertEquals("Order", meta.getAppId());
        assertEquals("com.example", meta.getNamespace());
        assertEquals(3, meta.getFields().size());

        FieldMetadata idField = meta.getFields().stream()
                .filter(FieldMetadata::isPrimaryKey).findFirst().orElseThrow();
        assertEquals("id", idField.getName());

        FieldMetadata cardField = meta.getFields().stream()
                .filter(f -> "cardNumber".equals(f.getName())).findFirst().orElseThrow();
        assertTrue(cardField.isSensitive());
        assertEquals("LAST_N", cardField.getMaskStrategy());
        assertEquals(4, cardField.getVisibleChars());
    }

    @Test
    void yamlParser_parsesBasicMetadata() {
        String yaml = "appId: test-app\n" +
                "displayName: Test App\n" +
                "version: \"2.0\"\n" +
                "fields:\n" +
                "  - name: userId\n" +
                "    type: String\n" +
                "    required: true\n" +
                "    primaryKey: true\n" +
                "  - name: email\n" +
                "    type: String\n" +
                "    sensitive: true\n" +
                "    maskStrategy: FULL\n";

        YamlMetadataParser parser = new YamlMetadataParser();
        ApplicationMetadata meta = parser.parse(yaml);

        assertEquals("test-app", meta.getAppId());
        assertEquals("2.0", meta.getVersion());
        assertEquals(2, meta.getFields().size());

        FieldMetadata userId = meta.getFields().get(0);
        assertTrue(userId.isPrimaryKey());
        assertTrue(userId.isRequired());

        FieldMetadata email = meta.getFields().get(1);
        assertTrue(email.isSensitive());
        assertEquals("FULL", email.getMaskStrategy());
    }

    @Test
    void registry_storeAndRetrieve() {
        MetadataRegistry registry = new MetadataRegistry();

        ApplicationMetadata meta = new ApplicationMetadata();
        meta.setAppId("my-app");
        registry.register(meta);

        assertTrue(registry.contains("my-app"));
        Optional<ApplicationMetadata> found = registry.find("my-app");
        assertTrue(found.isPresent());
        assertEquals("my-app", found.get().getAppId());
    }

    @Test
    void registry_getUnknownThrows() {
        MetadataRegistry registry = new MetadataRegistry();
        assertThrows(IllegalArgumentException.class, () -> registry.get("missing"));
    }
}
