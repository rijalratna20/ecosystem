package com.primus.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Date/time utilities that always operate in UTC.
 */
public final class DateUtils {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

    private DateUtils() {}

    public static Instant nowUtc() {
        return Instant.now();
    }

    public static String formatIso(Instant instant) {
        return instant == null ? null : ISO_FORMATTER.format(instant);
    }

    public static Instant parseIso(String text) {
        if (text == null || text.isBlank()) return null;
        return Instant.parse(text);
    }

    public static boolean isExpired(Instant expiry) {
        return expiry != null && Instant.now().isAfter(expiry);
    }

    public static Instant plusSeconds(Instant base, long seconds) {
        return base == null ? null : base.plusSeconds(seconds);
    }
}
