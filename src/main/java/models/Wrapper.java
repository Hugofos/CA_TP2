package models;

import java.util.List;

public class Wrapper {
    private List<Attribute> attributes;
    private String publicKey;

    // Getters and Setters
    public List<Attribute> getAttributes() { return attributes; }
    public void setAttributes(List<Attribute> attributes) { this.attributes = attributes; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}
