package models;

import java.util.List;

public class Min_dcc {
    private List<String> commitment_values;
    private String description;
    private List<MinAttribute> attributes;
    private String publicKey;
    private Signature dccSignature;
    private Signature signature;

    // Getters and setters
    public List<String> getCommitment_values() { return commitment_values; }
    public void setCommitment_values(List<String> commitment_values) { this.commitment_values = commitment_values; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<MinAttribute> getAttributes() { return attributes; }
    public void setAttributes(List<MinAttribute> attributes) { this.attributes = attributes; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public Signature getDccSignature() { return dccSignature; }
    public void setDccSignature(Signature dccSignature) { this.dccSignature = dccSignature; }

    public Signature getSignature() { return signature; }
    public void setSignature(Signature signature) { this.signature = signature; }
}
