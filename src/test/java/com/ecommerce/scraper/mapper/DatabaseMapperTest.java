package com.ecommerce.scraper.mapper;

import com.ecommerce.scraper.model.Product;
import com.ecommerce.scraper.model.ProductSpecifications;
import com.ecommerce.scraper.model.ProductVariant;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple manual test for DatabaseMapper
 */
public class DatabaseMapperTest {

    public static void main(String[] args) {
        System.out.println("Testing DatabaseMapper...");
        
        DatabaseMapper mapper = new DatabaseMapper();
        
        // Create test product
        Product product = new Product();
        product.setBrand("Apple");
        product.setTitle("iPhone 11 xüsusiyyətləri");
        
        // Create specifications
        ProductSpecifications specs = new ProductSpecifications();
        specs.setOperatingSystem("iOS 13");
        specs.setProcessor("Apple A13 Bionic");
        specs.setRam("4 GB");
        specs.setScreenSize("6.1 inç");
        specs.setBattery("3110 mAh");
        product.setSpecifications(specs);
        
        // Create variants
        ProductVariant variant1 = new ProductVariant();
        variant1.setStorage("64 GB");
        variant1.setColor("Black");
        variant1.setPrice("1149.00");
        variant1.setCurrency("AZN");
        variant1.setSeller("Umico");
        variant1.setWebsite("birmarket.az");
        product.addVariant(variant1);
        
        ProductVariant variant2 = new ProductVariant();
        variant2.setStorage("128 GB");
        variant2.setColor("White");
        variant2.setPrice("1249.00");
        variant2.setCurrency("AZN");
        variant2.setSeller("Kontakt");
        product.addVariant(variant2);
        
        // Map to database
        List<Product> products = new ArrayList<>();
        products.add(product);
        
        DatabaseOutput output = mapper.mapToDatabase(products);
        
        // Verify results
        System.out.println("\n=== MAPPING RESULTS ===");
        System.out.println("Brands: " + output.getBrands().size());
        System.out.println("Products: " + output.getProducts().size());
        System.out.println("Variants: " + output.getProductVariants().size());
        System.out.println("Attributes: " + output.getProductAttributes().size());
        
        if (!output.getBrands().isEmpty()) {
            System.out.println("\nBrand: " + output.getBrands().get(0).getName());
            System.out.println("Slug: " + output.getBrands().get(0).getSlug());
        }
        
        if (!output.getProducts().isEmpty()) {
            System.out.println("\nProduct: " + output.getProducts().get(0).getName());
            System.out.println("Slug: " + output.getProducts().get(0).getSlug());
            System.out.println("Description: " + output.getProducts().get(0).getDescription());
            System.out.println("Keywords: " + output.getProducts().get(0).getKeywords());
        }
        
        if (!output.getProductVariants().isEmpty()) {
            System.out.println("\nVariant 1: " + output.getProductVariants().get(0).getVariantName());
            System.out.println("Slug: " + output.getProductVariants().get(0).getSlug());
            System.out.println("Is Default: " + output.getProductVariants().get(0).getIsDefault());
        }
        
        System.out.println("\n✅ Test completed successfully!");
    }
}
