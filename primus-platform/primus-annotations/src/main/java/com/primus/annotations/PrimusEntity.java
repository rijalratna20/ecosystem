package com.primus.annotations;

import java.lang.annotation.*;

/**
 * Marks a class as a Primus-managed entity that participates in export and retrieval flows.
 * The {@code name} attribute identifies the entity in metadata and contracts.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PrimusEntity {

    /** Logical name of this entity (defaults to simple class name). */
    String name() default "";

    /** Optional namespace / application identifier. */
    String namespace() default "";

    /** Human-readable description for documentation. */
    String description() default "";
}
