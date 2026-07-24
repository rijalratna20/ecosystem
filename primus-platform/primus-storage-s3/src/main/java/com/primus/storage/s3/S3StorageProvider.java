package com.primus.storage.s3;

import java.io.InputStream;

/**
 * SPI for cloud object-storage operations used by the Primus export pipeline.
 * Implementations wrap AWS SDK, Azure SDK, or GCS SDK behind this common interface.
 */
public interface S3StorageProvider {

    /**
     * Upload export data to the object store.
     *
     * @param key     the object key (path within the bucket)
     * @param data    the content stream
     * @param length  content length in bytes (-1 if unknown)
     * @return the fully-qualified object URI (e.g. {@code s3://bucket/key})
     */
    String upload(String key, InputStream data, long length);

    /**
     * Download export data from the object store.
     *
     * @param key the object key
     * @return content stream; caller is responsible for closing
     */
    InputStream download(String key);

    /**
     * Delete an object.  Should honour lifecycle / retention policies before deleting.
     *
     * @param key the object key
     */
    void delete(String key);

    /**
     * Check whether an object with the given key exists.
     *
     * @param key the object key
     * @return true if the object exists
     */
    boolean exists(String key);

    /**
     * Generate a pre-signed URL valid for {@code expirySeconds} seconds.
     * Used by the retrieval API to give clients temporary download access.
     *
     * @param key           the object key
     * @param expirySeconds TTL for the pre-signed URL
     * @return pre-signed URL string
     */
    String presignedUrl(String key, int expirySeconds);
}
