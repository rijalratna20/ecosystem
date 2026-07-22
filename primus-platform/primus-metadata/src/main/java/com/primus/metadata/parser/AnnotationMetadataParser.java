package com.primus.metadata.parser;

import com.primus.annotations.PrimusEntity;
import com.primus.annotations.PrimusField;
import com.primus.annotations.PrimusId;
import com.primus.annotations.Sensitive;
import com.primus.metadata.model.ApplicationMetadata;
import com.primus.metadata.model.FieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Derives {@link ApplicationMetadata} from Java class annotations.
 * Scans {@link PrimusEntity}, {@link PrimusField}, {@link PrimusId}, and {@link Sensitive}.
 */
public class AnnotationMetadataParser {

    private static final Logger log = LoggerFactory.getLogger(AnnotationMetadataParser.class);

    public ApplicationMetadata parse(Class<?> entityClass) {
        PrimusEntity entityAnnotation = entityClass.getAnnotation(PrimusEntity.class);
        if (entityAnnotation == null) {
            throw new IllegalArgumentException(
                    entityClass.getName() + " is not annotated with @PrimusEntity");
        }

        ApplicationMetadata meta = new ApplicationMetadata();
        String name = entityAnnotation.name().isBlank()
                ? entityClass.getSimpleName()
                : entityAnnotation.name();
        meta.setAppId(name);
        meta.setDisplayName(name);
        meta.setNamespace(entityAnnotation.namespace());
        meta.setRootEntityClass(entityClass.getName());
        meta.setVersion("1.0");

        List<FieldMetadata> fields = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            PrimusField pf = field.getAnnotation(PrimusField.class);
            PrimusId pid = field.getAnnotation(PrimusId.class);
            if (pf == null && pid == null) continue;

            FieldMetadata fm = new FieldMetadata();
            String fieldName = (pf != null && !pf.name().isBlank()) ? pf.name() : field.getName();
            if (pid != null && !pid.name().isBlank()) fieldName = pid.name();
            fm.setName(fieldName);
            fm.setJavaType(field.getType().getSimpleName());
            fm.setPrimaryKey(pid != null);

            if (pf != null) {
                fm.setRequired(pf.required());
                fm.setFormat(pf.format());
                fm.setDescription(pf.description());
            }

            Sensitive sensitive = field.getAnnotation(Sensitive.class);
            if (sensitive != null) {
                fm.setSensitive(true);
                fm.setMaskStrategy(sensitive.strategy().name());
                fm.setVisibleChars(sensitive.visibleChars());
            }

            fields.add(fm);
        }
        meta.setFields(fields);
        return meta;
    }
}
