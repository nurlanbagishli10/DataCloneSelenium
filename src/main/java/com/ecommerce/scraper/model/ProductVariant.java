package com.ecommerce.scraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductVariant {

    @JsonProperty("storage")
    private String storage;

    @JsonProperty("color")
    private String color;

    @JsonProperty("price")
    private String price;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("seller")
    private String seller;

    @JsonProperty("website")
    private String website;

    // Constructors
    public ProductVariant() {}

    public ProductVariant(String storage, String color, String price) {
        this.storage = storage;
        this.color = color;
        this.price = price;
    }

    // Getters and Setters
    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "ProductVariant{" +
                "storage='" + storage + '\'' +
                ", color='" + color + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}