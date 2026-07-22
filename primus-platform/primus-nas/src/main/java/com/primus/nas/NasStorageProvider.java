package com.primus.nas;

import com.primus.common.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Pattern;

/**
 * NAS (filesystem-based) storage provider.
 *
 * <p>Uses atomic write semantics: data is written to a {@code .tmp} file and then
 * moved into place to avoid partial writes. SHA-256 checksums are stored alongside
 * each artifact for integrity verification.
 *
 * <p>Directory structure:
 * <pre>
 *   {rootPath}/{appId}/{exportId}/data.{format}
 *   {rootPath}/{appId}/{exportId}/data.{format}.sha256
 * </pre>
 *
 * <p><b>Security:</b> {@code appId}, {@code exportId}, and {@code format} are
 * validated against a safe alphanumeric pattern before constructing any filesystem path,
 * preventing path-traversal attacks.
 */
public class NasStorageProvider {

    private static final Logger log = LoggerFactory.getLogger(NasStorageProvider.class);

    /** Allow only safe path segment characters: alphanumeric, hyphens, underscores, dots. */
    private static final Pattern SAFE_SEGMENT = Pattern.compile("^[a-zA-Z0-9._\\-]+$");

    private final Path rootPath;

    public NasStorageProvider(Path rootPath) {
        this.rootPath = rootPath.toAbsolutePath().normalize();
    }

    /**
     * Store {@code content} at the given path within the NAS root.
     * The write is atomic: content goes to a temp file and is then moved.
     *
     * @param appId    application identifier
     * @param exportId export identifier
     * @param format   file extension / format (e.g. "json")
     * @param content  raw bytes to store
     * @return the SHA-256 hex checksum of the stored content
     */
    public String put(String appId, String exportId, String format, byte[] content) {
        validateSegment("appId", appId);
        validateSegment("exportId", exportId);
        validateSegment("format", format);

        Path dir = resolveDir(appId, exportId);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new StorageException("Failed to create storage directory", e);
        }

        String checksum = sha256(content);
        Path target = dir.resolve("data." + format);
        Path tmp = dir.resolve("data." + format + ".tmp");

        try {
            Files.write(tmp, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            Files.writeString(dir.resolve("data." + format + ".sha256"), checksum + "\n");
        } catch (IOException e) {
            throw new StorageException("Failed to write artifact", e);
        }

        log.debug("Stored artifact appId={} exportId={} format={} bytes={} checksum={}",
                appId, exportId, format, content.length, checksum);
        return checksum;
    }

    /**
     * Read an artifact. Verifies the stored checksum before returning.
     *
     * @return the artifact bytes
     * @throws StorageException if the file is missing or the checksum does not match
     */
    public byte[] get(String appId, String exportId, String format) {
        validateSegment("appId", appId);
        validateSegment("exportId", exportId);
        validateSegment("format", format);

        Path target = resolveDir(appId, exportId).resolve("data." + format);
        if (!Files.exists(target)) {
            throw new StorageException("Artifact not found for exportId=" + exportId);
        }

        try {
            byte[] content = Files.readAllBytes(target);
            verifyChecksum(appId, exportId, format, content);
            return content;
        } catch (IOException e) {
            throw new StorageException("Failed to read artifact", e);
        }
    }

    /**
     * Delete all artifacts for an export.
     */
    public void delete(String appId, String exportId) {
        validateSegment("appId", appId);
        validateSegment("exportId", exportId);

        Path dir = resolveDir(appId, exportId);
        if (!Files.exists(dir)) return;
        try {
            try (var stream = Files.walk(dir)) {
                stream.sorted(java.util.Comparator.reverseOrder())
                        .forEach(p -> {
                            try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                        });
            }
        } catch (IOException e) {
            log.warn("Failed to delete artifacts for exportId={}: {}", exportId, e.getMessage());
        }
    }

    /** Return true if an artifact exists for the given key. */
    public boolean exists(String appId, String exportId, String format) {
        validateSegment("appId", appId);
        validateSegment("exportId", exportId);
        validateSegment("format", format);
        return Files.exists(resolveDir(appId, exportId).resolve("data." + format));
    }

    // -------------------------------------------------------------------------
    // internals

    /**
     * Validate that a path segment contains only safe characters to prevent path traversal.
     */
    private static void validateSegment(String name, String value) {
        if (value == null || value.isBlank()) {
            throw new StorageException("Storage path segment '" + name + "' must not be blank");
        }
        if (!SAFE_SEGMENT.matcher(value).matches()) {
            throw new StorageException("Storage path segment '" + name
                    + "' contains unsafe characters: " + value);
        }
    }

    private Path resolveDir(String appId, String exportId) {
        Path dir = rootPath.resolve(appId).resolve(exportId).normalize();
        // Double-check the resolved path is still within rootPath (defense-in-depth)
        if (!dir.startsWith(rootPath)) {
            throw new StorageException("Resolved path escapes storage root");
        }
        return dir;
    }

    private void verifyChecksum(String appId, String exportId, String format, byte[] content) {
        Path checksumFile = resolveDir(appId, exportId).resolve("data." + format + ".sha256");
        if (!Files.exists(checksumFile)) {
            log.warn("No checksum file found for appId={} exportId={}", appId, exportId);
            return;
        }
        try {
            String stored = Files.readString(checksumFile).trim();
            String computed = sha256(content);
            if (!stored.equals(computed)) {
                throw new StorageException("Checksum mismatch for exportId=" + exportId)
                        .withDetail("errorCode", "INTEGRITY_CHECK_FAILED");
            }
        } catch (IOException e) {
            log.warn("Could not read checksum file: {}", e.getMessage());
        }
    }

    static String sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
