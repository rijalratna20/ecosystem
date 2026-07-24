package com.primus.common.util;

import java.util.regex.Pattern;

/**
 * General-purpose string utilities used across Primus modules.
 */
public final class StringUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private StringUtils() {}

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUuid(String value) {
        return value != null && UUID_PATTERN.matcher(value).matches();
    }

    /**
     * Converts a camelCase or PascalCase string to snake_case.
     * E.g. "UserProfile" -> "user_profile"
     */
    public static String toSnakeCase(String input) {
        if (isBlank(input)) return input;
        return input
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z\\d])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    /**
     * Converts a snake_case string to camelCase.
     * E.g. "user_profile" -> "userProfile"
     */
    public static String toCamelCase(String input) {
        if (isBlank(input)) return input;
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : input.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Truncates a string to maxLength, appending "..." if truncated. */
    public static String truncate(String input, int maxLength) {
        if (input == null || input.length() <= maxLength) return input;
        return input.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    /** Masks all but the last {@code visibleChars} characters with '*'. */
    public static String mask(String input, int visibleChars) {
        if (isBlank(input)) return input;
        int len = input.length();
        if (visibleChars >= len) return "*".repeat(len);
        return "*".repeat(len - visibleChars) + input.substring(len - visibleChars);
    }
}
