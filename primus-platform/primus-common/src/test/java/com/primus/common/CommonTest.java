package com.primus.common;

import com.primus.common.dto.ApiResponse;
import com.primus.common.dto.Page;
import com.primus.common.exception.NotFoundException;
import com.primus.common.exception.ValidationException;
import com.primus.common.util.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommonTest {

    @Test
    void apiResponse_ok() {
        ApiResponse<String> resp = ApiResponse.ok("hello");
        assertTrue(resp.isSuccess());
        assertEquals(200, resp.getCode());
        assertEquals("hello", resp.getData());
        assertNull(resp.getError());
        assertNotNull(resp.getMetadata());
    }

    @Test
    void apiResponse_error() {
        ApiResponse<Void> resp = ApiResponse.error("VALIDATION_ERROR", "bad input", 400);
        assertFalse(resp.isSuccess());
        assertEquals(400, resp.getCode());
        assertEquals("VALIDATION_ERROR", resp.getError().getCode());
    }

    @Test
    void page_hasNext() {
        Page<String> page = Page.of(List.of("a", "b"), 10, 2, 0);
        assertEquals(5, page.getTotalPages());
        assertTrue(page.hasNext());
        assertFalse(page.hasPrevious());
    }

    @Test
    void page_empty() {
        Page<String> page = Page.empty(10);
        assertTrue(page.isEmpty());
        assertEquals(0, page.getTotalPages());
    }

    @Test
    void stringUtils_toSnakeCase() {
        assertEquals("user_profile", StringUtils.toSnakeCase("UserProfile"));
        assertEquals("my_field_name", StringUtils.toSnakeCase("myFieldName"));
    }

    @Test
    void stringUtils_toCamelCase() {
        assertEquals("userProfile", StringUtils.toCamelCase("user_profile"));
    }

    @Test
    void stringUtils_mask() {
        assertEquals("****5678", StringUtils.mask("12345678", 4));
        assertEquals("****", StringUtils.mask("1234", 0));
    }

    @Test
    void notFoundException_hasDetails() {
        NotFoundException ex = new NotFoundException("Export", "abc-123");
        assertEquals("NOT_FOUND", ex.getErrorCode());
        assertTrue(ex.getMessage().contains("abc-123"));
        assertEquals("Export", ex.getDetails().get("resourceType"));
    }

    @Test
    void validationException() {
        ValidationException ex = new ValidationException("field required");
        assertEquals("VALIDATION_ERROR", ex.getErrorCode());
    }
}
