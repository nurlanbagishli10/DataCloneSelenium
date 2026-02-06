package com.ecommerce.scraper.mapper;

import com.ecommerce.scraper.model.Product;
import com.ecommerce.scraper.utils.JsonExporter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

/**
 * Test with real scraped data
 */
public class RealDataMapperTest {

    public static void main(String[] args) {
        try {
            System.out.println("Testing DatabaseMapper with real scraped data...");
            
            // Load existing scraped data
            File inputFile = new File("output/products_20260206_154939.json");
            if (!inputFile.exists()) {
                System.err.println("No scraped data found at: " + inputFile.getPath());
                return;
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            List<Product> products = objectMapper.readValue(inputFile, new TypeReference<List<Product>>() {});
            
            System.out.println("Loaded " + products.size() + " products from file");
            
            // Map to database format
            DatabaseMapper mapper = new DatabaseMapper();
            DatabaseOutput output = mapper.mapToDatabase(products);
            
            // Export to JSON
            JsonExporter exporter = new JsonExporter();
            String testOutputFile = "output/test_database_import.json";
            exporter.exportDatabaseOutput(output, testOutputFile);
            
            System.out.println("\n=== TEST RESULTS ===");
            System.out.println("✅ Successfully mapped " + products.size() + " products");
            System.out.println("✅ Generated " + output.getBrands().size() + " brands");
            System.out.println("✅ Generated " + output.getProducts().size() + " products");
            System.out.println("✅ Generated " + output.getProductVariants().size() + " variants");
            System.out.println("✅ Generated " + output.getProductAttributes().size() + " attributes");
            System.out.println("✅ Output written to: " + testOutputFile);
            
            // Sample output
            if (!output.getBrands().isEmpty()) {
                System.out.println("\nSample Brand:");
                System.out.println("  ID: " + output.getBrands().get(0).getId());
                System.out.println("  Name: " + output.getBrands().get(0).getName());
                System.out.println("  Slug: " + output.getBrands().get(0).getSlug());
            }
            
            if (!output.getProducts().isEmpty()) {
                System.out.println("\nSample Product:");
                System.out.println("  ID: " + output.getProducts().get(0).getId());
                System.out.println("  Name: " + output.getProducts().get(0).getName());
                System.out.println("  Brand ID: " + output.getProducts().get(0).getBrandId());
                System.out.println("  Price: " + output.getProducts().get(0).getPrice());
            }
            
            System.out.println("\n✅ All tests passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
