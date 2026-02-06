package com.ecommerce.scraper.mapper;

import com.ecommerce.scraper.model.AttributeGroup;
import com.ecommerce.scraper.model.ProductSpecifications;
import com.ecommerce.scraper.model.SpecificationItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple manual test for AttributeMapper
 */
public class AttributeMapperTest {

    public static void main(String[] args) {
        System.out.println("Testing AttributeMapper...");
        
        AttributeMapper mapper = new AttributeMapper();
        
        // Create test specifications similar to iPhone 11
        ProductSpecifications specs = new ProductSpecifications();
        
        // Direct specifications
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
        
        // Map to attributes
        List<AttributeGroup> attributes = mapper.mapToAttributes(specs);
        
        // Print results
        System.out.println("\n=== ATTRIBUTE MAPPING RESULTS ===\n");
        System.out.println("Total attribute groups: " + attributes.size());
        System.out.println();
        
        for (AttributeGroup group : attributes) {
            System.out.println("Category: " + group.getMainLabel());
            System.out.println("Specifications count: " + group.getItems().size());
            
            for (SpecificationItem item : group.getItems()) {
                String valueDisplay = item.getValue();
                if (valueDisplay.length() > 50) {
                    valueDisplay = valueDisplay.substring(0, 47) + "...";
                }
                System.out.println("  - " + item.getLabel() + " (" + item.getKey() + "): " + valueDisplay);
            }
            System.out.println();
        }
        
        // Verify expected categories
        boolean hasUmumi = attributes.stream().anyMatch(g -> g.getMainLabel().equals("ümumi_xüsusiyyətlər"));
        boolean hasEkran = attributes.stream().anyMatch(g -> g.getMainLabel().equals("ekran"));
        boolean hasKamera = attributes.stream().anyMatch(g -> g.getMainLabel().equals("kamera"));
        boolean hasYaddash = attributes.stream().anyMatch(g -> g.getMainLabel().equals("yaddaş"));
        boolean hasBatareya = attributes.stream().anyMatch(g -> g.getMainLabel().equals("batareya"));
        boolean hasOlchular = attributes.stream().anyMatch(g -> g.getMainLabel().equals("ölçülər_və_çəki"));
        boolean hasRabite = attributes.stream().anyMatch(g -> g.getMainLabel().equals("rabitə"));
        boolean hasDayaniqlilik = attributes.stream().anyMatch(g -> g.getMainLabel().equals("dayanıqlılıq"));
        boolean hasElave = attributes.stream().anyMatch(g -> g.getMainLabel().equals("əlavə_xüsusiyyətlər"));
        
        System.out.println("=== VERIFICATION ===");
        System.out.println("Has ümumi_xüsusiyyətlər: " + hasUmumi);
        System.out.println("Has ekran: " + hasEkran);
        System.out.println("Has kamera: " + hasKamera);
        System.out.println("Has yaddaş: " + hasYaddash);
        System.out.println("Has batareya: " + hasBatareya);
        System.out.println("Has ölçülər_və_çəki: " + hasOlchular);
        System.out.println("Has rabitə: " + hasRabite);
        System.out.println("Has dayanıqlılıq: " + hasDayaniqlilik);
        System.out.println("Has əlavə_xüsusiyyətlər: " + hasElave);
        
        if (hasUmumi && hasEkran && hasKamera && hasYaddash && hasBatareya && 
            hasOlchular && hasRabite && hasDayaniqlilik && hasElave) {
            System.out.println("\n✅ Test completed successfully!");
        } else {
            System.out.println("\n❌ Test failed - some categories are missing!");
        }
    }
}
