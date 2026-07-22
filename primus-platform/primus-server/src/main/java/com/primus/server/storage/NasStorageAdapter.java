package com.primus.server.storage;

import com.primus.nas.NasStorageProvider;
import org.springframework.stereotype.Component;

/**
 * Adapter that makes {@link NasStorageProvider} implement the {@link StorageProvider} SPI.
 */
@Component
public class NasStorageAdapter implements StorageProvider {

    private final NasStorageProvider nas;

    public NasStorageAdapter(NasStorageProvider nas) {
        this.nas = nas;
    }

    @Override
    public String put(String appId, String exportId, String format, byte[] content) {
        return nas.put(appId, exportId, format, content);
    }

    @Override
    public byte[] get(String appId, String exportId, String format) {
        return nas.get(appId, exportId, format);
    }

    @Override
    public boolean exists(String appId, String exportId, String format) {
        return nas.exists(appId, exportId, format);
    }

    @Override
    public void delete(String appId, String exportId) {
        nas.delete(appId, exportId);
    }
}
