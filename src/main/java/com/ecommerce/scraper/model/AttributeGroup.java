package com.ecommerce.scraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class AttributeGroup {

    @JsonProperty("main_label")
    private String mainLabel;

    @JsonProperty("items")
    private List<SpecificationItem> items;

    // Constructors
    public AttributeGroup() {
        this.items = new ArrayList<>();
    }

    public AttributeGroup(String mainLabel) {
        this.mainLabel = mainLabel;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public String getMainLabel() {
        return mainLabel;
    }

    public void setMainLabel(String mainLabel) {
        this.mainLabel = mainLabel;
    }

    public List<SpecificationItem> getItems() {
        return items;
    }

    public void setItems(List<SpecificationItem> items) {
        this.items = items;
    }

    public void addItem(SpecificationItem item) {
        this.items.add(item);
    }
}
