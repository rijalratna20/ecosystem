package com.primus.annotations;

import java.lang.annotation.*;

/**
 * Marks the primary key field of a {@link PrimusEntity}.
 * Exactly one field per entity must carry this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface PrimusId {
    /** Override the exported field name for the ID (default: "id"). */
    String name() default "id";
}
