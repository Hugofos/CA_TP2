package models;

public class Signature {
    private String value;
    private String timestamp;
    private String description;
    private String certificate;

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCertificate() { return certificate; }
    public void setCertificate(String certificate) { this.certificate = certificate; }
}
