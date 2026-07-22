package com.primus.annotations;

import java.lang.annotation.*;

/**
 * Marks a field (or getter) to be included in a Primus export payload.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface PrimusField {

    /** Export alias. Defaults to the Java field name. */
    String name() default "";

    /** Whether this field must be non-null in a valid export. */
    boolean required() default false;

    /** Optional date/time format pattern (for temporal fields). */
    String format() default "";

    /** Optional description for documentation. */
    String description() default "";
}
