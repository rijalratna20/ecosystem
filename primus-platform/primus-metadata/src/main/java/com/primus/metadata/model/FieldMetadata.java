package com.primus.metadata.model;

/**
 * Describes a single exportable field within an application entity.
 */
public class FieldMetadata {

    private String name;
    private String javaType;
    private boolean required;
    private boolean sensitive;
    private String maskStrategy;     // FULL | LAST_N | REDACT
    private int visibleChars;
    private String format;
    private boolean primaryKey;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getJavaType() { return javaType; }
    public void setJavaType(String javaType) { this.javaType = javaType; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public boolean isSensitive() { return sensitive; }
    public void setSensitive(boolean sensitive) { this.sensitive = sensitive; }

    public String getMaskStrategy() { return maskStrategy; }
    public void setMaskStrategy(String maskStrategy) { this.maskStrategy = maskStrategy; }

    public int getVisibleChars() { return visibleChars; }
    public void setVisibleChars(int visibleChars) { this.visibleChars = visibleChars; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public boolean isPrimaryKey() { return primaryKey; }
    public void setPrimaryKey(boolean primaryKey) { this.primaryKey = primaryKey; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
