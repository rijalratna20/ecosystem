package com.primus.nas;

import com.primus.common.exception.StorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class NasStorageProviderTest {

    @TempDir
    Path tempDir;

    @Test
    void putAndGet_roundTrip() {
        NasStorageProvider nas = new NasStorageProvider(tempDir);
        byte[] content = "{\"key\":\"value\"}".getBytes();

        String checksum = nas.put("app1", "export-001", "json", content);
        assertNotNull(checksum);
        assertFalse(checksum.isBlank());

        byte[] retrieved = nas.get("app1", "export-001", "json");
        assertArrayEquals(content, retrieved);
    }

    @Test
    void exists_trueAfterPut() {
        NasStorageProvider nas = new NasStorageProvider(tempDir);
        assertFalse(nas.exists("a", "b", "json"));

        nas.put("a", "b", "json", "data".getBytes());
        assertTrue(nas.exists("a", "b", "json"));
    }

    @Test
    void get_missingFile_throws() {
        NasStorageProvider nas = new NasStorageProvider(tempDir);
        assertThrows(StorageException.class, () -> nas.get("app", "missing", "json"));
    }

    @Test
    void delete_removesArtifacts() {
        NasStorageProvider nas = new NasStorageProvider(tempDir);
        nas.put("app", "exp", "json", "hello".getBytes());
        assertTrue(nas.exists("app", "exp", "json"));

        nas.delete("app", "exp");
        assertFalse(nas.exists("app", "exp", "json"));
    }

    @Test
    void checksumMismatch_throwsStorageException() {
        NasStorageProvider nas = new NasStorageProvider(tempDir);
        byte[] original = "original content".getBytes();
        nas.put("app", "exp2", "json", original);

        // Tamper: overwrite the data file directly
        Path dataFile = tempDir.resolve("app/exp2/data.json");
        assertDoesNotThrow(() -> java.nio.file.Files.writeString(dataFile, "tampered"));

        assertThrows(StorageException.class, () -> nas.get("app", "exp2", "json"));
    }

    @Test
    void sha256_deterministic() {
        byte[] data = "hello world".getBytes();
        String h1 = NasStorageProvider.sha256(data);
        String h2 = NasStorageProvider.sha256(data);
        assertEquals(h1, h2);
        assertEquals(64, h1.length()); // SHA-256 hex = 64 chars
    }
}
