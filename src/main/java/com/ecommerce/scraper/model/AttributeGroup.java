package com.ecommerce.scraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class AttributeGroup {

    @JsonProperty("main_label")
    private String mainLabel;

    @JsonProperty("specifications")
    private List<SpecificationItem> specifications;

    // Constructors
    public AttributeGroup() {
        this.specifications = new ArrayList<>();
    }

    public AttributeGroup(String mainLabel) {
        this.mainLabel = mainLabel;
        this.specifications = new ArrayList<>();
    }

    // Getters and Setters
    public String getMainLabel() {
        return mainLabel;
    }

    public void setMainLabel(String mainLabel) {
        this.mainLabel = mainLabel;
    }

    public List<SpecificationItem> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<SpecificationItem> specifications) {
        this.specifications = specifications;
    }

    public void addSpecification(SpecificationItem specification) {
        this.specifications.add(specification);
    }
}
