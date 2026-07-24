package com.primus.storage.s3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory stub implementation of {@link S3StorageProvider}.
 * Used for local development and unit testing. Not suitable for production.
 */
public class StubS3StorageProvider implements S3StorageProvider {

    private final Map<String, byte[]> store = new HashMap<>();
    private final String bucketName;

    public StubS3StorageProvider(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public String upload(String key, InputStream data, long length) {
        try {
            store.put(key, data.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Stub upload failed for key: " + key, e);
        }
        return "s3://" + bucketName + "/" + key;
    }

    @Override
    public InputStream download(String key) {
        byte[] data = store.get(key);
        if (data == null) throw new IllegalArgumentException("Object not found: " + key);
        return new ByteArrayInputStream(data);
    }

    @Override
    public void delete(String key) {
        store.remove(key);
    }

    @Override
    public boolean exists(String key) {
        return store.containsKey(key);
    }

    @Override
    public String presignedUrl(String key, int expirySeconds) {
        return "https://stub-s3.local/" + bucketName + "/" + key + "?expires=" + expirySeconds;
    }
}
