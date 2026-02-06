package com.ecommerce.scraper.mapper;

import com.ecommerce.scraper.model.db.BrandEntity;
import com.ecommerce.scraper.model.db.ProductAttributeEntity;
import com.ecommerce.scraper.model.db.ProductEntity;
import com.ecommerce.scraper.model.db.ProductVariantEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DatabaseOutput {

    @JsonProperty("brands")
    private List<BrandEntity> brands = new ArrayList<>();

    @JsonProperty("products")
    private List<ProductEntity> products = new ArrayList<>();

    @JsonProperty("product_variants")
    private List<ProductVariantEntity> productVariants = new ArrayList<>();

    @JsonProperty("product_attributes")
    private List<ProductAttributeEntity> productAttributes = new ArrayList<>();

    // Constructors
    public DatabaseOutput() {}

    // Getters and Setters
    public List<BrandEntity> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandEntity> brands) {
        this.brands = brands;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }

    public List<ProductVariantEntity> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(List<ProductVariantEntity> productVariants) {
        this.productVariants = productVariants;
    }

    public List<ProductAttributeEntity> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(List<ProductAttributeEntity> productAttributes) {
        this.productAttributes = productAttributes;
    }

    // Helper methods to add entities
    public void addBrand(BrandEntity brand) {
        this.brands.add(brand);
    }

    public void addProduct(ProductEntity product) {
        this.products.add(product);
    }

    public void addProductVariant(ProductVariantEntity variant) {
        this.productVariants.add(variant);
    }

    public void addProductAttribute(ProductAttributeEntity attribute) {
        this.productAttributes.add(attribute);
    }
}
