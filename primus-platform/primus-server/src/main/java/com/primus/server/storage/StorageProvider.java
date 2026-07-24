package com.primus.server.storage;

/**
 * Storage provider SPI. Core services interact only with this interface,
 * allowing the NAS implementation to be swapped for cloud storage (Tier 3).
 */
public interface StorageProvider {

    /**
     * Store content and return its checksum.
     *
     * @param appId    application identifier
     * @param exportId export identifier
     * @param format   file format extension (e.g. "json")
     * @param content  raw bytes
     * @return SHA-256 hex checksum
     */
    String put(String appId, String exportId, String format, byte[] content);

    /**
     * Retrieve content. Implementations must verify integrity before returning.
     */
    byte[] get(String appId, String exportId, String format);

    /** Returns true if an artifact exists. */
    boolean exists(String appId, String exportId, String format);

    /** Delete all artifacts for an export. */
    void delete(String appId, String exportId);
}
