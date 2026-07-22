package com.primus.annotations;

import java.lang.annotation.*;

/**
 * Marks a field containing sensitive data. The export pipeline will apply
 * the configured masking strategy before including the value in output.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Sensitive {

    /** Masking strategy to apply. Defaults to full masking. */
    MaskStrategy strategy() default MaskStrategy.FULL;

    /**
     * For {@link MaskStrategy#LAST_N}, how many trailing characters remain visible.
     * Ignored for other strategies.
     */
    int visibleChars() default 4;

    enum MaskStrategy {
        /** Replace all characters with '*'. */
        FULL,
        /** Keep the last N characters visible, mask the rest. */
        LAST_N,
        /** Replace with a static placeholder string. */
        REDACT
    }
}
