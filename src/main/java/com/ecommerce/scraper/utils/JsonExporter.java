package com.ecommerce.scraper.utils;

import com.ecommerce.scraper.mapper.DatabaseOutput;
import com.ecommerce.scraper.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JsonExporter {

    private static final Logger logger = LoggerFactory.getLogger(JsonExporter.class);
    private final ObjectMapper objectMapper;
    private final ObjectMapper databaseObjectMapper;

    public JsonExporter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Configure ObjectMapper for database output with LocalDateTime support
        this.databaseObjectMapper = new ObjectMapper();
        this.databaseObjectMapper.registerModule(new JavaTimeModule());
        this.databaseObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.databaseObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Məhsulları JSON fayla yaz
     */
    public void exportToJson(List<Product> products, String outputPath) {
        try {
            File outputFile = new File(outputPath);

            // Parent directory yarat
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            objectMapper.writeValue(outputFile, products);

            logger.info("✅ {} ədəd məhsul JSON-a yazıldı: {}",
                    products.size(), outputPath);

        } catch (IOException e) {
            logger.error("JSON fayla yazma xətası: {}", e.getMessage(), e);
        }
    }

    /**
     * Timestamp ilə fayl adı yarat
     */
    public static String generateOutputFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return String.format("output/products_%s.json", timestamp);
    }

    /**
     * Export database output to JSON file
     */
    public void exportDatabaseOutput(DatabaseOutput dbOutput, String outputPath) {
        try {
            File outputFile = new File(outputPath);

            // Parent directory yarat
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            databaseObjectMapper.writeValue(outputFile, dbOutput);

            logger.info("✅ Database output yazıldı: {} (Brands: {}, Products: {}, Variants: {}, Attributes: {})",
                    outputPath,
                    dbOutput.getBrands().size(),
                    dbOutput.getProducts().size(),
                    dbOutput.getProductVariants().size(),
                    dbOutput.getProductAttributes().size());

        } catch (IOException e) {
            logger.error("Database JSON fayla yazma xətası: {}", e.getMessage(), e);
        }
    }

    /**
     * Tək məhsulu JSON-a çevir (debug üçün)
     */
    public String toJsonString(Product product) {
        try {
            return objectMapper.writeValueAsString(product);
        } catch (IOException e) {
            logger.error("JSON string-ə çevirmə xətası: {}", e.getMessage());
            return "{}";
        }
    }
}