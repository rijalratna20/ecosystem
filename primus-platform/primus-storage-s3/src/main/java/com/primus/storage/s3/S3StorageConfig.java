package com.primus.storage.s3;

/**
 * Configuration for an S3-compatible object storage backend.
 *
 * <p>Example YAML (Spring Boot):
 * <pre>
 * primus.storage.s3:
 *   enabled: true
 *   bucket: primus-exports
 *   region: us-east-1
 *   endpoint: https://s3.amazonaws.com   # override for MinIO / GCS
 *   accessKey: ${AWS_ACCESS_KEY_ID}
 *   secretKey: ${AWS_SECRET_ACCESS_KEY}
 *   replication:
 *     enabled: true
 *     regions: [eu-west-1, ap-southeast-1]
 *   lifecycle:
 *     transitionDays: 90
 *     deleteAfterDays: 365
 * </pre>
 */
public final class S3StorageConfig {

    private final boolean enabled;
    private final String bucket;
    private final String region;
    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final boolean replicationEnabled;
    private final int lifecycleTransitionDays;
    private final int lifecycleDeleteAfterDays;

    private S3StorageConfig(Builder b) {
        this.enabled = b.enabled;
        this.bucket = b.bucket;
        this.region = b.region;
        this.endpoint = b.endpoint;
        this.accessKey = b.accessKey;
        this.secretKey = b.secretKey;
        this.replicationEnabled = b.replicationEnabled;
        this.lifecycleTransitionDays = b.lifecycleTransitionDays;
        this.lifecycleDeleteAfterDays = b.lifecycleDeleteAfterDays;
    }

    public boolean isEnabled() { return enabled; }
    public String getBucket() { return bucket; }
    public String getRegion() { return region; }
    public String getEndpoint() { return endpoint; }
    public String getAccessKey() { return accessKey; }
    public String getSecretKey() { return secretKey; }
    public boolean isReplicationEnabled() { return replicationEnabled; }
    public int getLifecycleTransitionDays() { return lifecycleTransitionDays; }
    public int getLifecycleDeleteAfterDays() { return lifecycleDeleteAfterDays; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private boolean enabled = true;
        private String bucket = "primus-exports";
        private String region = "us-east-1";
        private String endpoint = "https://s3.amazonaws.com";
        private String accessKey;
        private String secretKey;
        private boolean replicationEnabled = false;
        private int lifecycleTransitionDays = 90;
        private int lifecycleDeleteAfterDays = 365;

        public Builder enabled(boolean v) { this.enabled = v; return this; }
        public Builder bucket(String v) { this.bucket = v; return this; }
        public Builder region(String v) { this.region = v; return this; }
        public Builder endpoint(String v) { this.endpoint = v; return this; }
        public Builder accessKey(String v) { this.accessKey = v; return this; }
        public Builder secretKey(String v) { this.secretKey = v; return this; }
        public Builder replicationEnabled(boolean v) { this.replicationEnabled = v; return this; }
        public Builder lifecycleTransitionDays(int v) { this.lifecycleTransitionDays = v; return this; }
        public Builder lifecycleDeleteAfterDays(int v) { this.lifecycleDeleteAfterDays = v; return this; }

        public S3StorageConfig build() {
            if (bucket == null || bucket.isBlank()) throw new IllegalStateException("bucket is required");
            if (region == null || region.isBlank()) throw new IllegalStateException("region is required");
            return new S3StorageConfig(this);
        }
    }
}
