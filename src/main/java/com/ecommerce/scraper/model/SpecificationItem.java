package com.ecommerce.scraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpecificationItem {

    @JsonProperty("label")
    private String label;  // Azerbaijani display name

    @JsonProperty("key")
    private String key;    // English field key

    @JsonProperty("value")
    private String value;  // Actual value

    // Constructors
    public SpecificationItem() {}

    public SpecificationItem(String label, String key, String value) {
        this.label = label;
        this.key = key;
        this.value = value;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
