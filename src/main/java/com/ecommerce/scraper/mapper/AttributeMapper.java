package com.ecommerce.scraper.mapper;

import com.ecommerce.scraper.model.AttributeGroup;
import com.ecommerce.scraper.model.ProductSpecifications;
import com.ecommerce.scraper.model.SpecificationItem;

import java.util.*;
import java.util.Map.Entry;

public class AttributeMapper {

    // Azerbaijani label mappings
    private static final Map<String, String> AZERBAIJANI_LABELS = Map.ofEntries(
        Map.entry("operating_system", "Əməliyyat sistemi"),
        Map.entry("processor", "Prosessor"),
        Map.entry("max_processor_speed", "Maksimum prosessor sürəti"),
        Map.entry("processor_count", "Prosessorların sayı"),
        Map.entry("graphic_processor", "Qrafik prosessor"),
        Map.entry("screen_size", "Ekran ölçüsü"),
        Map.entry("screen_type", "Ekran tipi"),
        Map.entry("refresh_rate", "Yenilənmə tezliyi"),
        Map.entry("resolution", "Həlledicilik"),
        Map.entry("back_camera_mp", "Arxa kamera"),
        Map.entry("back_camera_count", "Arxa kamera sayı"),
        Map.entry("front_camera_mp", "Ön kamera"),
        Map.entry("video_resolution", "Video çəkmə"),
        Map.entry("ram", "RAM"),
        Map.entry("battery", "Batareya tutumu"),
        Map.entry("weight", "Çəkisi"),
        Map.entry("length", "Uzunluğu"),
        Map.entry("width", "Eni"),
        Map.entry("thickness", "Qalınlığı")
    );

    // Category mappings - direct specifications
    private static final Map<String, List<String>> CATEGORY_MAPPINGS = Map.ofEntries(
        Map.entry("ümumi_xüsusiyyətlər", Arrays.asList(
            "operating_system", "processor", "max_processor_speed", 
            "processor_count", "graphic_processor"
        )),
        Map.entry("ekran", Arrays.asList(
            "screen_size", "screen_type", "refresh_rate", "resolution"
        )),
        Map.entry("kamera", Arrays.asList(
            "back_camera_mp", "back_camera_count", "front_camera_mp", "video_resolution"
        )),
        Map.entry("yaddaş", Arrays.asList("ram")),
        Map.entry("batareya", Arrays.asList("battery")),
        Map.entry("ölçülər_və_çəki", Arrays.asList("weight", "length", "width", "thickness"))
    );

    // Additional specs category mappings (Azerbaijani field names to categories)
    private static final Map<String, String> ADDITIONAL_SPECS_CATEGORIES = Map.ofEntries(
        // ümumi_xüsusiyyətlər
        Map.entry("İstehsal ili", "ümumi_xüsusiyyətlər"),
        Map.entry("Funksiyalar", "ümumi_xüsusiyyətlər"),
        
        // ekran
        Map.entry("Ekran qorunması", "ekran"),
        Map.entry("Maksimum ekran parlaqlığı", "ekran"),
        Map.entry("Ekran gövdə faizi", "ekran"),
        Map.entry("Sensor ekran (Touchscreen)", "ekran"),
        Map.entry("Sensor ekran", "ekran"),
        
        // kamera
        Map.entry("Saniyədə Kadr Sayı (FPS)", "kamera"),
        Map.entry("Saniyədə Kadr Sayı", "kamera"),
        Map.entry("Şəkil sabitləşdirilməsi", "kamera"),
        
        // yaddaş
        Map.entry("Daxili yaddaş", "yaddaş"),
        Map.entry("Yaddaş kartı dəstəyi", "yaddaş"),
        
        // batareya
        Map.entry("Simsiz enerji toplama", "batareya"),
        
        // rabitə
        Map.entry("5G", "rabitə"),
        Map.entry("4G", "rabitə"),
        Map.entry("NFC", "rabitə"),
        Map.entry("SIM kart sayı", "rabitə"),
        Map.entry("SIM kart tipi", "rabitə"),
        
        // dayanıqlılıq
        Map.entry("Suya davamlı", "dayanıqlılıq"),
        Map.entry("Suya davamlılıq", "dayanıqlılıq"),
        Map.entry("Toza davamlılıq", "dayanıqlılıq"),
        Map.entry("Suya və toza davamlılıq standartı", "dayanıqlılıq"),
        
        // əlavə_xüsusiyyətlər
        Map.entry("Rəng", "əlavə_xüsusiyyətlər"),
        Map.entry("Qələm", "əlavə_xüsusiyyətlər"),
        Map.entry("Qatlana bilən", "əlavə_xüsusiyyətlər")
    );

    // Additional specs key mappings (Azerbaijani to English keys)
    private static final Map<String, String> ADDITIONAL_SPECS_KEY_MAPPINGS = Map.ofEntries(
        Map.entry("İstehsal ili", "production_year"),
        Map.entry("Funksiyalar", "features"),
        Map.entry("Ekran qorunması", "screen_protection"),
        Map.entry("Maksimum ekran parlaqlığı", "max_brightness"),
        Map.entry("Ekran gövdə faizi", "screen_to_body_ratio"),
        Map.entry("Sensor ekran (Touchscreen)", "touchscreen"),
        Map.entry("Sensor ekran", "touchscreen"),
        Map.entry("Saniyədə Kadr Sayı (FPS)", "fps"),
        Map.entry("Saniyədə Kadr Sayı", "fps"),
        Map.entry("Şəkil sabitləşdirilməsi", "image_stabilization"),
        Map.entry("Daxili yaddaş", "internal_storage"),
        Map.entry("Yaddaş kartı dəstəyi", "memory_card_support"),
        Map.entry("Simsiz enerji toplama", "wireless_charging"),
        Map.entry("5G", "has_5g"),
        Map.entry("4G", "has_4g"),
        Map.entry("NFC", "nfc"),
        Map.entry("SIM kart sayı", "sim_count"),
        Map.entry("SIM kart tipi", "sim_type"),
        Map.entry("Suya davamlı", "water_resistant"),
        Map.entry("Suya davamlılıq", "water_resistance"),
        Map.entry("Toza davamlılıq", "dust_resistance"),
        Map.entry("Suya və toza davamlılıq standartı", "ip_rating"),
        Map.entry("Rəng", "colors"),
        Map.entry("Qələm", "has_pen"),
        Map.entry("Qatlana bilən", "foldable")
    );

    /**
     * Main mapping method: converts ProductSpecifications to List<AttributeGroup>
     */
    public List<AttributeGroup> mapToAttributes(ProductSpecifications specs) {
        if (specs == null) {
            return new ArrayList<>();
        }

        Map<String, AttributeGroup> categoryGroups = new LinkedHashMap<>();

        // Initialize all category groups
        initializeCategoryGroups(categoryGroups);

        // Map direct specifications
        mapDirectSpecifications(specs, categoryGroups);

        // Map dimensions
        mapDimensions(specs, categoryGroups);

        // Map additional specs
        mapAdditionalSpecs(specs, categoryGroups);

        // Convert to list and remove empty groups
        List<AttributeGroup> result = new ArrayList<>();
        for (AttributeGroup group : categoryGroups.values()) {
            if (!group.getItems().isEmpty()) {
                result.add(group);
            }
        }

        return result;
    }

    /**
     * Initialize all category groups in order
     */
    private void initializeCategoryGroups(Map<String, AttributeGroup> categoryGroups) {
        categoryGroups.put("ümumi_xüsusiyyətlər", new AttributeGroup("ümumi_xüsusiyyətlər"));
        categoryGroups.put("ekran", new AttributeGroup("ekran"));
        categoryGroups.put("kamera", new AttributeGroup("kamera"));
        categoryGroups.put("yaddaş", new AttributeGroup("yaddaş"));
        categoryGroups.put("batareya", new AttributeGroup("batareya"));
        categoryGroups.put("ölçülər_və_çəki", new AttributeGroup("ölçülər_və_çəki"));
        categoryGroups.put("rabitə", new AttributeGroup("rabitə"));
        categoryGroups.put("dayanıqlılıq", new AttributeGroup("dayanıqlılıq"));
        categoryGroups.put("əlavə_xüsusiyyətlər", new AttributeGroup("əlavə_xüsusiyyətlər"));
    }

    /**
     * Map direct specifications from ProductSpecifications object
     */
    private void mapDirectSpecifications(ProductSpecifications specs, 
                                        Map<String, AttributeGroup> categoryGroups) {
        // Operating System
        addSpecIfNotNull(categoryGroups, "ümumi_xüsusiyyətlər", 
            "operating_system", specs.getOperatingSystem());
        
        // Processor
        addSpecIfNotNull(categoryGroups, "ümumi_xüsusiyyətlər", 
            "processor", specs.getProcessor());
        
        // Max Processor Speed
        addSpecIfNotNull(categoryGroups, "ümumi_xüsusiyyətlər", 
            "max_processor_speed", specs.getMaxProcessorSpeed());
        
        // Processor Count
        addSpecIfNotNull(categoryGroups, "ümumi_xüsusiyyətlər", 
            "processor_count", specs.getProcessorCount());
        
        // Graphic Processor
        addSpecIfNotNull(categoryGroups, "ümumi_xüsusiyyətlər", 
            "graphic_processor", specs.getGraphicProcessor());
        
        // Screen Size
        addSpecIfNotNull(categoryGroups, "ekran", 
            "screen_size", specs.getScreenSize());
        
        // Screen Type
        addSpecIfNotNull(categoryGroups, "ekran", 
            "screen_type", specs.getScreenType());
        
        // Refresh Rate
        addSpecIfNotNull(categoryGroups, "ekran", 
            "refresh_rate", specs.getRefreshRate());
        
        // Resolution
        addSpecIfNotNull(categoryGroups, "ekran", 
            "resolution", specs.getResolution());
        
        // Back Camera MP
        addSpecIfNotNull(categoryGroups, "kamera", 
            "back_camera_mp", specs.getBackCameraMp());
        
        // Back Camera Count
        addSpecIfNotNull(categoryGroups, "kamera", 
            "back_camera_count", specs.getBackCameraCount());
        
        // Front Camera MP
        addSpecIfNotNull(categoryGroups, "kamera", 
            "front_camera_mp", specs.getFrontCameraMp());
        
        // Video Resolution
        addSpecIfNotNull(categoryGroups, "kamera", 
            "video_resolution", specs.getVideoResolution());
        
        // RAM
        addSpecIfNotNull(categoryGroups, "yaddaş", 
            "ram", specs.getRam());
        
        // Battery
        addSpecIfNotNull(categoryGroups, "batareya", 
            "battery", specs.getBattery());
        
        // Weight
        addSpecIfNotNull(categoryGroups, "ölçülər_və_çəki", 
            "weight", specs.getWeight());
    }

    /**
     * Map dimensions to ölçülər_və_çəki category
     */
    private void mapDimensions(ProductSpecifications specs, 
                              Map<String, AttributeGroup> categoryGroups) {
        if (specs.getDimensions() == null || specs.getDimensions().isEmpty()) {
            return;
        }

        Map<String, String> dimensions = specs.getDimensions();
        
        // Length
        if (dimensions.containsKey("length")) {
            addSpecIfNotNull(categoryGroups, "ölçülər_və_çəki", 
                "length", dimensions.get("length"));
        }
        
        // Width
        if (dimensions.containsKey("width")) {
            addSpecIfNotNull(categoryGroups, "ölçülər_və_çəki", 
                "width", dimensions.get("width"));
        }
        
        // Thickness
        if (dimensions.containsKey("thickness")) {
            addSpecIfNotNull(categoryGroups, "ölçülər_və_çəki", 
                "thickness", dimensions.get("thickness"));
        }
    }

    /**
     * Map additional specs to appropriate categories
     */
    private void mapAdditionalSpecs(ProductSpecifications specs, 
                                   Map<String, AttributeGroup> categoryGroups) {
        if (specs.getAdditionalSpecs() == null || specs.getAdditionalSpecs().isEmpty()) {
            return;
        }

        for (Entry<String, String> entry : specs.getAdditionalSpecs().entrySet()) {
            String azerbaijaniLabel = entry.getKey();
            String value = entry.getValue();
            
            // Get category for this field
            String category = ADDITIONAL_SPECS_CATEGORIES.getOrDefault(
                azerbaijaniLabel, "əlavə_xüsusiyyətlər");
            
            // Get English key for this field
            String key = ADDITIONAL_SPECS_KEY_MAPPINGS.getOrDefault(
                azerbaijaniLabel, convertToKey(azerbaijaniLabel));
            
            // Add specification
            AttributeGroup group = categoryGroups.get(category);
            if (group != null) {
                group.addItem(new SpecificationItem(
                    azerbaijaniLabel, key, getValueOrEmpty(value)));
            }
        }
    }

    /**
     * Helper method to add specification if value is not null
     */
    private void addSpecIfNotNull(Map<String, AttributeGroup> categoryGroups,
                                 String category, String key, String value) {
        AttributeGroup group = categoryGroups.get(category);
        if (group != null) {
            String label = AZERBAIJANI_LABELS.getOrDefault(key, key);
            group.addItem(new SpecificationItem(
                label, key, getValueOrEmpty(value)));
        }
    }

    /**
     * Helper method to return empty string if value is null
     */
    private String getValueOrEmpty(String value) {
        return value != null ? value : "";
    }

    /**
     * Convert Azerbaijani label to English key
     */
    private String convertToKey(String label) {
        if (label == null) {
            return "";
        }
        
        // Use StringBuilder for efficient string manipulation
        StringBuilder result = new StringBuilder();
        for (char c : label.toLowerCase().toCharArray()) {
            switch (c) {
                case 'ə':
                    result.append('a');
                    break;
                case 'ı':
                    result.append('i');
                    break;
                case 'ö':
                    result.append('o');
                    break;
                case 'ü':
                    result.append('u');
                    break;
                case 'ğ':
                    result.append('g');
                    break;
                case 'ş':
                    result.append("sh");
                    break;
                case 'ç':
                    result.append("ch");
                    break;
                case ' ':
                    result.append('_');
                    break;
                default:
                    // Only keep alphanumeric and underscore
                    if (Character.isLetterOrDigit(c) || c == '_') {
                        result.append(c);
                    }
                    break;
            }
        }
        
        return result.toString();
    }
}
