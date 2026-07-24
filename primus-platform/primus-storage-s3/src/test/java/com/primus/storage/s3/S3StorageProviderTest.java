package com.primus.storage.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class S3StorageProviderTest {

    private S3StorageProvider provider;

    @BeforeEach
    void setUp() {
        provider = new StubS3StorageProvider("test-bucket");
    }

    @Test
    void upload_and_download() throws Exception {
        byte[] content = "export-data".getBytes(StandardCharsets.UTF_8);
        String uri = provider.upload("exports/2024/01/01/file.json",
                new ByteArrayInputStream(content), content.length);

        assertTrue(uri.startsWith("s3://test-bucket/"));

        InputStream result = provider.download("exports/2024/01/01/file.json");
        assertArrayEquals(content, result.readAllBytes());
    }

    @Test
    void exists_after_upload() {
        provider.upload("key1", new ByteArrayInputStream(new byte[]{1}), 1);
        assertTrue(provider.exists("key1"));
        assertFalse(provider.exists("key2"));
    }

    @Test
    void delete_removes_object() {
        provider.upload("key3", new ByteArrayInputStream(new byte[]{1}), 1);
        provider.delete("key3");
        assertFalse(provider.exists("key3"));
    }

    @Test
    void presigned_url_contains_key() {
        String url = provider.presignedUrl("exports/test.json", 3600);
        assertTrue(url.contains("exports/test.json"));
    }

    @Test
    void download_missing_key_throws() {
        assertThrows(IllegalArgumentException.class, () -> provider.download("nonexistent"));
    }

    @Test
    void config_builder_defaults() {
        S3StorageConfig cfg = S3StorageConfig.builder()
                .accessKey("AKID")
                .secretKey("SECRET")
                .build();
        assertEquals("primus-exports", cfg.getBucket());
        assertEquals("us-east-1", cfg.getRegion());
        assertEquals(90, cfg.getLifecycleTransitionDays());
    }
}
