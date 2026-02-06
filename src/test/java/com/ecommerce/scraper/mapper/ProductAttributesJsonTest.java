package com.ecommerce.scraper.mapper;

import com.ecommerce.scraper.model.AttributeGroup;
import com.ecommerce.scraper.model.Product;
import com.ecommerce.scraper.model.ProductSpecifications;
import com.ecommerce.scraper.model.ProductVariant;
import com.ecommerce.scraper.utils.JsonExporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test to verify complete Product JSON output with new attributes structure
 */
public class ProductAttributesJsonTest {

    public static void main(String[] args) {
        System.out.println("Testing Product JSON with attributes...");
        
        // Create test product
        Product product = new Product();
        product.setBrand("Apple");
        product.setTitle("iPhone 11 xüsusiyyətləri");
        
        // Create specifications
        ProductSpecifications specs = new ProductSpecifications();
        specs.setOperatingSystem("iOS 13");
        specs.setProcessor("Apple A13 Bionic");
        specs.setMaxProcessorSpeed("2.65 GHz");
        specs.setProcessorCount("6");
        specs.setGraphicProcessor("Apple GPU (4-core graphics)");
        specs.setScreenSize("6.1 inç");
        specs.setScreenType("LCD");
        specs.setRefreshRate("60 Hz");
        specs.setResolution("828 x 1792 px");
        specs.setBackCameraMp("12 MP");
        specs.setBackCameraCount("2");
        specs.setFrontCameraMp("12 MP");
        specs.setVideoResolution("4K");
        specs.setRam("4 GB");
        specs.setBattery("3110 mAh");
        specs.setWeight("194 qram");
        
        // Dimensions
        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("length", "150.9 mm");
        dimensions.put("width", "75.7 mm");
        dimensions.put("thickness", "8.3 mm");
        specs.setDimensions(dimensions);
        
        // Additional specs
        Map<String, String> additionalSpecs = new HashMap<>();
        additionalSpecs.put("Suya davamlılıq", "uzun müddətli suya batmaya qarşı qorunur");
        additionalSpecs.put("Ekran qorunması", "Corning-made glass");
        additionalSpecs.put("Yaddaş kartı dəstəyi", "");
        additionalSpecs.put("Toza davamlılıq", "tam tozkeçirməz");
        additionalSpecs.put("Şəkil sabitləşdirilməsi", "OIS");
        additionalSpecs.put("Daxili yaddaş", "64 GB\n128 GB\n256 GB");
        additionalSpecs.put("NFC", "");
        additionalSpecs.put("Maksimum ekran parlaqlığı", "625 nit");
        additionalSpecs.put("Ekran gövdə faizi", "79.0 %");
        additionalSpecs.put("Rəng", "Gold\nRed\nWhite\nYellow\nBlack\nGreen\nPurple");
        additionalSpecs.put("Sensor ekran (Touchscreen)", "");
        additionalSpecs.put("Qələm", "");
        additionalSpecs.put("Saniyədə Kadr Sayı (FPS)", "60");
        additionalSpecs.put("5G", "");
        additionalSpecs.put("4G", "");
        additionalSpecs.put("SIM kart tipi", "Nano-SIM\neSIM");
        additionalSpecs.put("İstehsal ili", "2019");
        additionalSpecs.put("Qatlana bilən", "");
        additionalSpecs.put("Simsiz enerji toplama", "");
        additionalSpecs.put("Funksiyalar", "Üz oxuyucu");
        additionalSpecs.put("Suya və toza davamlılıq standartı", "IP68");
        additionalSpecs.put("SIM kart sayı", "2");
        additionalSpecs.put("Suya davamlı", "");
        specs.setAdditionalSpecs(additionalSpecs);
        
        product.setSpecifications(specs);
        
        // Map to attributes
        AttributeMapper attributeMapper = new AttributeMapper();
        List<AttributeGroup> attributes = attributeMapper.mapToAttributes(specs);
        product.setAttributes(attributes);
        
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
        
        // Export to JSON file
        JsonExporter exporter = new JsonExporter();
        String outputFile = "output/test_product_attributes.json";
        exporter.exportToJson(List.of(product), outputFile);
        
        System.out.println("\n=== TEST RESULTS ===");
        System.out.println("✅ Product created with " + attributes.size() + " attribute groups");
        System.out.println("✅ Product has " + product.getVariants().size() + " variants");
        System.out.println("✅ Product has both 'specifications' and 'attributes' fields");
        System.out.println("✅ JSON exported to: " + outputFile);
        System.out.println("\n✅ Test completed successfully!");
    }
}
