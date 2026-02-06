package com.ecommerce.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Product {

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("title")
    private String title;

    @JsonProperty("variants")
    private List<ProductVariant> variants = new ArrayList<>();

    @JsonIgnore  // Hide from JSON export, keep for internal use
    private ProductSpecifications specifications;

    @JsonProperty("attributes")
    private List<AttributeGroup> attributes;

    // Constructors
    public Product() {}

    public Product(String brand, String title) {
        this.brand = brand;
        this.title = title;
    }

    // Getters and Setters
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public void addVariant(ProductVariant variant) {
        this.variants.add(variant);
    }

    public ProductSpecifications getSpecifications() {
        return specifications;
    }

    public void setSpecifications(ProductSpecifications specifications) {
        this.specifications = specifications;
    }

    public List<AttributeGroup> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeGroup> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Product{" +
                "brand='" + brand + '\'' +
                ", title='" + title + '\'' +
                ", variants=" + variants.size() +
                '}';
    }
}