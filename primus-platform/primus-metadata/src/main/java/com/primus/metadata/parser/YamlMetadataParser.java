package com.primus.metadata.parser;

import com.primus.metadata.model.ApplicationMetadata;
import com.primus.metadata.model.FieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parses application metadata from YAML contract definitions.
 *
 * <p>Expected YAML structure:
 * <pre>
 * appId: my-app
 * displayName: My Application
 * version: "1.0"
 * namespace: com.example
 * fields:
 *   - name: userId
 *     type: String
 *     required: true
 *     primaryKey: true
 *   - name: ssn
 *     type: String
 *     sensitive: true
 *     maskStrategy: LAST_N
 *     visibleChars: 4
 * </pre>
 *
 * <p><b>Security:</b> Uses {@link SafeConstructor} to prevent unsafe deserialization
 * of arbitrary Java types from untrusted YAML input.
 */
public class YamlMetadataParser {

    private static final Logger log = LoggerFactory.getLogger(YamlMetadataParser.class);

    /** Create a safe Yaml instance that only loads primitive/collection types. */
    private static Yaml safeYaml() {
        return new Yaml(new SafeConstructor(new LoaderOptions()));
    }

    public ApplicationMetadata parse(InputStream input) {
        @SuppressWarnings("unchecked")
        Map<String, Object> doc = (Map<String, Object>) safeYaml().load(input);
        return fromMap(doc);
    }

    public ApplicationMetadata parse(String yamlContent) {
        @SuppressWarnings("unchecked")
        Map<String, Object> doc = (Map<String, Object>) safeYaml().load(yamlContent);
        return fromMap(doc);
    }

    @SuppressWarnings("unchecked")
    private ApplicationMetadata fromMap(Map<String, Object> doc) {
        ApplicationMetadata meta = new ApplicationMetadata();
        meta.setAppId((String) doc.get("appId"));
        meta.setDisplayName((String) doc.getOrDefault("displayName", meta.getAppId()));
        meta.setVersion((String) doc.getOrDefault("version", "1.0"));
        meta.setNamespace((String) doc.getOrDefault("namespace", ""));
        meta.setRootEntityClass((String) doc.getOrDefault("rootEntityClass", ""));

        List<Map<String, Object>> rawFields =
                (List<Map<String, Object>>) doc.getOrDefault("fields", new ArrayList<>());
        List<FieldMetadata> fields = new ArrayList<>();
        for (Map<String, Object> rawField : rawFields) {
            fields.add(parseField(rawField));
        }
        meta.setFields(fields);
        return meta;
    }

    private FieldMetadata parseField(Map<String, Object> raw) {
        FieldMetadata field = new FieldMetadata();
        field.setName((String) raw.get("name"));
        field.setJavaType((String) raw.getOrDefault("type", "String"));
        field.setRequired(Boolean.TRUE.equals(raw.getOrDefault("required", false)));
        field.setSensitive(Boolean.TRUE.equals(raw.getOrDefault("sensitive", false)));
        field.setPrimaryKey(Boolean.TRUE.equals(raw.getOrDefault("primaryKey", false)));
        field.setFormat((String) raw.getOrDefault("format", ""));
        field.setDescription((String) raw.getOrDefault("description", ""));
        field.setMaskStrategy((String) raw.getOrDefault("maskStrategy", "FULL"));
        Object visibleChars = raw.getOrDefault("visibleChars", 4);
        field.setVisibleChars(visibleChars instanceof Number ? ((Number) visibleChars).intValue() : 4);
        return field;
    }
}
