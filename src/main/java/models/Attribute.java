package models;

public class Attribute {
    private String label;
    private String value;
    private String commitment;
    private String mask;

    // Getters and Setters
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getCommitment() { return commitment; }
    public void setCommitment(String commitment) { this.commitment = commitment; }

    public String getMask() { return mask; }
    public void setMask(String mask) { this.mask = mask; }
}
