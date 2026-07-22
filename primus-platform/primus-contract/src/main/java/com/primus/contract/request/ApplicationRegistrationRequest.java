package com.primus.contract.request;

import java.util.Map;

/**
 * Request to register or update an application in the Primus registry.
 */
public class ApplicationRegistrationRequest {

    private String appId;
    private String displayName;
    private String ownerTeam;
    private String contactEmail;
    private String metadataYaml;
    private Map<String, String> tags;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getOwnerTeam() { return ownerTeam; }
    public void setOwnerTeam(String ownerTeam) { this.ownerTeam = ownerTeam; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getMetadataYaml() { return metadataYaml; }
    public void setMetadataYaml(String metadataYaml) { this.metadataYaml = metadataYaml; }

    public Map<String, String> getTags() { return tags; }
    public void setTags(Map<String, String> tags) { this.tags = tags; }
}
