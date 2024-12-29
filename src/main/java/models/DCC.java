package models;

import java.util.List;

public class DCC {
    private List<Attribute> attributes;
    private String description;
    private String publicKey;
    private Signature signature;

    // Getters and Setters
    public List<Attribute> getAttributes() { return attributes; }
    public void setAttributes(List<Attribute> attributes) { this.attributes = attributes; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public Signature getSignature() { return signature; }
    public void setSignature(Signature signature) { this.signature = signature; }
}
