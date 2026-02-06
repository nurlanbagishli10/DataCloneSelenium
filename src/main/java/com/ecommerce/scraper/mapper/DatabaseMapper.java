package com.ecommerce.scraper.mapper;

import com.ecommerce.scraper.model.Product;
import com.ecommerce.scraper.model.ProductSpecifications;
import com.ecommerce.scraper.model.ProductVariant;
import com.ecommerce.scraper.model.db.BrandEntity;
import com.ecommerce.scraper.model.db.ProductAttributeEntity;
import com.ecommerce.scraper.model.db.ProductEntity;
import com.ecommerce.scraper.model.db.ProductVariantEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class DatabaseMapper {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMapper.class);

    private final AtomicLong brandIdCounter = new AtomicLong(1);
    private final AtomicLong productIdCounter = new AtomicLong(1);
    private final AtomicLong variantIdCounter = new AtomicLong(1);
    private final AtomicLong attributeIdCounter = new AtomicLong(1);

    private final Map<String, BrandEntity> brandCache = new HashMap<>();

    /**
     * Main mapping method: converts list of Products to DatabaseOutput
     */
    public DatabaseOutput mapToDatabase(List<Product> products) {
        logger.info("Starting database mapping for {} products", products.size());
        
        DatabaseOutput output = new DatabaseOutput();

        for (Product product : products) {
            if (product == null) {
                continue;
            }

            // Get or create brand
            BrandEntity brand = getOrCreateBrand(product.getBrand());
            
            // Map product
            ProductEntity productEntity = mapToProductEntity(product, brand.getId());
            output.addProduct(productEntity);

            // Map variants
            if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                for (int i = 0; i < product.getVariants().size(); i++) {
                    ProductVariant variant = product.getVariants().get(i);
                    ProductVariantEntity variantEntity = mapToVariantEntity(variant, productEntity.getId());
                    
                    // Set first variant as default
                    if (i == 0) {
                        variantEntity.setIsDefault(true);
                    }
                    
                    output.addProductVariant(variantEntity);

                    // Create variant attributes
                    createVariantAttributes(variant, productEntity.getId(), variantEntity.getId(), output);
                }
            }

            // Create product attributes from specifications
            createProductAttributes(product, productEntity.getId(), output);
        }

        // Add all brands to output
        output.setBrands(brandCache.values().stream().toList());

        logger.info("Database mapping completed: {} brands, {} products, {} variants, {} attributes",
                output.getBrands().size(), output.getProducts().size(),
                output.getProductVariants().size(), output.getProductAttributes().size());

        return output;
    }

    /**
     * Get existing brand or create new one
     */
    public BrandEntity getOrCreateBrand(String brandName) {
        if (brandName == null || brandName.trim().isEmpty()) {
            brandName = "Unknown";
        }

        String brandKey = brandName.trim().toLowerCase();
        
        if (!brandCache.containsKey(brandKey)) {
            BrandEntity brand = new BrandEntity(brandIdCounter.getAndIncrement(), brandName.trim());
            brandCache.put(brandKey, brand);
            logger.debug("Created new brand: {}", brandName);
        }

        return brandCache.get(brandKey);
    }

    /**
     * Map Product to ProductEntity
     */
    public ProductEntity mapToProductEntity(Product product, Long brandId) {
        Long productId = productIdCounter.getAndIncrement();
        ProductEntity entity = new ProductEntity(productId, product.getTitle());

        entity.setBrandId(brandId);
        entity.setExternalBrandName(product.getBrand());

        // Generate description from specifications
        String description = buildDescription(product.getSpecifications());
        entity.setDescription(description);
        
        // Set short description (first 200 chars of description)
        if (description != null && !description.isEmpty()) {
            entity.setShortDescription(description.length() > 200 
                ? description.substring(0, 200) + "..." 
                : description);
        }

        // Generate keywords
        String keywords = generateKeywords(product);
        entity.setKeywords(keywords);

        // Set price from first variant if available
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            ProductVariant firstVariant = product.getVariants().get(0);
            entity.setPrice(parsePrice(firstVariant.getPrice()));
        }

        // Generate SKU
        entity.setSku(generateSku(product));

        return entity;
    }

    /**
     * Map ProductVariant to ProductVariantEntity
     */
    public ProductVariantEntity mapToVariantEntity(ProductVariant variant, Long productId) {
        Long variantId = variantIdCounter.getAndIncrement();
        
        // Build variant name from storage and color
        String variantName = buildVariantName(variant);
        
        ProductVariantEntity entity = new ProductVariantEntity(variantId, productId, variantName);

        entity.setPrice(parsePrice(variant.getPrice()));
        entity.setSku(generateVariantSku(variant));
        
        // Generate keywords from variant name
        entity.setKeywords(variantName != null ? variantName.toLowerCase() : "");

        return entity;
    }

    /**
     * Create variant attributes (storage, color)
     */
    public void createVariantAttributes(ProductVariant variant, Long productId, Long variantId, DatabaseOutput output) {
        // Storage attribute
        if (variant.getStorage() != null && !variant.getStorage().trim().isEmpty()) {
            ProductAttributeEntity storageAttr = new ProductAttributeEntity(
                    attributeIdCounter.getAndIncrement(),
                    productId,
                    "storage",
                    variant.getStorage().trim()
            );
            storageAttr.setVariantId(variantId);
            storageAttr.setAttributeType("variant");
            output.addProductAttribute(storageAttr);
        }

        // Color attribute
        if (variant.getColor() != null && !variant.getColor().trim().isEmpty()) {
            ProductAttributeEntity colorAttr = new ProductAttributeEntity(
                    attributeIdCounter.getAndIncrement(),
                    productId,
                    "color",
                    variant.getColor().trim()
            );
            colorAttr.setVariantId(variantId);
            colorAttr.setAttributeType("variant");
            output.addProductAttribute(colorAttr);
        }

        // Seller info attribute
        if (variant.getSeller() != null && !variant.getSeller().trim().isEmpty()) {
            ProductAttributeEntity sellerAttr = new ProductAttributeEntity(
                    attributeIdCounter.getAndIncrement(),
                    productId,
                    "seller",
                    variant.getSeller().trim()
            );
            sellerAttr.setVariantId(variantId);
            sellerAttr.setAttributeType("seller_info");
            output.addProductAttribute(sellerAttr);
        }

        // Website attribute
        if (variant.getWebsite() != null && !variant.getWebsite().trim().isEmpty()) {
            ProductAttributeEntity websiteAttr = new ProductAttributeEntity(
                    attributeIdCounter.getAndIncrement(),
                    productId,
                    "website",
                    variant.getWebsite().trim()
            );
            websiteAttr.setVariantId(variantId);
            websiteAttr.setAttributeType("seller_info");
            output.addProductAttribute(websiteAttr);
        }

        // Currency attribute
        if (variant.getCurrency() != null && !variant.getCurrency().trim().isEmpty()) {
            ProductAttributeEntity currencyAttr = new ProductAttributeEntity(
                    attributeIdCounter.getAndIncrement(),
                    productId,
                    "currency",
                    variant.getCurrency().trim()
            );
            currencyAttr.setVariantId(variantId);
            currencyAttr.setAttributeType("seller_info");
            output.addProductAttribute(currencyAttr);
        }
    }

    /**
     * Create product attributes from specifications
     */
    public void createProductAttributes(Product product, Long productId, DatabaseOutput output) {
        ProductSpecifications specs = product.getSpecifications();
        if (specs == null) {
            return;
        }

        int sortOrder = 1;

        // Operating System
        if (specs.getOperatingSystem() != null && !specs.getOperatingSystem().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "operating_system", specs.getOperatingSystem(), sortOrder++);
        }

        // Processor
        if (specs.getProcessor() != null && !specs.getProcessor().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "processor", specs.getProcessor(), sortOrder++);
        }

        // Max Processor Speed
        if (specs.getMaxProcessorSpeed() != null && !specs.getMaxProcessorSpeed().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "max_processor_speed", specs.getMaxProcessorSpeed(), sortOrder++);
        }

        // Processor Count
        if (specs.getProcessorCount() != null && !specs.getProcessorCount().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "processor_count", specs.getProcessorCount(), sortOrder++);
        }

        // Graphic Processor
        if (specs.getGraphicProcessor() != null && !specs.getGraphicProcessor().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "graphic_processor", specs.getGraphicProcessor(), sortOrder++);
        }

        // Screen Size
        if (specs.getScreenSize() != null && !specs.getScreenSize().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "screen_size", specs.getScreenSize(), sortOrder++);
        }

        // Screen Type
        if (specs.getScreenType() != null && !specs.getScreenType().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "screen_type", specs.getScreenType(), sortOrder++);
        }

        // Refresh Rate
        if (specs.getRefreshRate() != null && !specs.getRefreshRate().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "refresh_rate", specs.getRefreshRate(), sortOrder++);
        }

        // Resolution
        if (specs.getResolution() != null && !specs.getResolution().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "resolution", specs.getResolution(), sortOrder++);
        }

        // Back Camera MP
        if (specs.getBackCameraMp() != null && !specs.getBackCameraMp().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "back_camera_mp", specs.getBackCameraMp(), sortOrder++);
        }

        // Back Camera Count
        if (specs.getBackCameraCount() != null && !specs.getBackCameraCount().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "back_camera_count", specs.getBackCameraCount(), sortOrder++);
        }

        // Front Camera MP
        if (specs.getFrontCameraMp() != null && !specs.getFrontCameraMp().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "front_camera_mp", specs.getFrontCameraMp(), sortOrder++);
        }

        // Video Resolution
        if (specs.getVideoResolution() != null && !specs.getVideoResolution().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "video_resolution", specs.getVideoResolution(), sortOrder++);
        }

        // RAM
        if (specs.getRam() != null && !specs.getRam().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "ram", specs.getRam(), sortOrder++);
        }

        // Battery
        if (specs.getBattery() != null && !specs.getBattery().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "battery", specs.getBattery(), sortOrder++);
        }

        // Weight
        if (specs.getWeight() != null && !specs.getWeight().trim().isEmpty()) {
            addSpecificationAttribute(output, productId, "weight", specs.getWeight(), sortOrder++);
        }

        // Dimensions
        if (specs.getDimensions() != null && !specs.getDimensions().isEmpty()) {
            for (Map.Entry<String, String> entry : specs.getDimensions().entrySet()) {
                if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                    addSpecificationAttribute(output, productId, "dimension_" + entry.getKey(), entry.getValue(), sortOrder++);
                }
            }
        }

        // Additional Specs
        if (specs.getAdditionalSpecs() != null && !specs.getAdditionalSpecs().isEmpty()) {
            for (Map.Entry<String, String> entry : specs.getAdditionalSpecs().entrySet()) {
                if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                    ProductAttributeEntity attr = new ProductAttributeEntity(
                            attributeIdCounter.getAndIncrement(),
                            productId,
                            entry.getKey(),
                            entry.getValue().trim()
                    );
                    attr.setAttributeType("additional");
                    attr.setSortOrder(sortOrder++);
                    output.addProductAttribute(attr);
                }
            }
        }
    }

    /**
     * Helper method to add specification attribute
     */
    private void addSpecificationAttribute(DatabaseOutput output, Long productId, String name, String value, int sortOrder) {
        ProductAttributeEntity attr = new ProductAttributeEntity(
                attributeIdCounter.getAndIncrement(),
                productId,
                name,
                value.trim()
        );
        attr.setAttributeType("specification");
        attr.setSortOrder(sortOrder);
        output.addProductAttribute(attr);
    }

    /**
     * Build description from specifications
     */
    private String buildDescription(ProductSpecifications specs) {
        if (specs == null) {
            return "";
        }

        StringBuilder desc = new StringBuilder();

        if (specs.getProcessor() != null && !specs.getProcessor().isEmpty()) {
            desc.append("Processor: ").append(specs.getProcessor()).append(". ");
        }
        if (specs.getRam() != null && !specs.getRam().isEmpty()) {
            desc.append("RAM: ").append(specs.getRam()).append(". ");
        }
        if (specs.getScreenSize() != null && !specs.getScreenSize().isEmpty()) {
            desc.append("Screen: ").append(specs.getScreenSize()).append(". ");
        }
        if (specs.getBattery() != null && !specs.getBattery().isEmpty()) {
            desc.append("Battery: ").append(specs.getBattery()).append(". ");
        }
        if (specs.getBackCameraMp() != null && !specs.getBackCameraMp().isEmpty()) {
            desc.append("Camera: ").append(specs.getBackCameraMp()).append(". ");
        }

        return desc.toString().trim();
    }

    /**
     * Generate keywords from product
     */
    private String generateKeywords(Product product) {
        StringBuilder keywords = new StringBuilder();

        if (product.getBrand() != null && !product.getBrand().isEmpty()) {
            keywords.append(product.getBrand().toLowerCase()).append(", ");
        }

        if (product.getTitle() != null && !product.getTitle().isEmpty()) {
            // Extract main words from title
            String[] words = product.getTitle().toLowerCase().split("\\s+");
            for (String word : words) {
                if (word.length() > 3) { // Only meaningful words
                    keywords.append(word).append(", ");
                }
            }
        }

        String result = keywords.toString().trim();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    /**
     * Build variant name from storage and color
     */
    private String buildVariantName(ProductVariant variant) {
        StringBuilder name = new StringBuilder();

        if (variant.getStorage() != null && !variant.getStorage().trim().isEmpty()) {
            name.append(variant.getStorage().trim());
        }

        if (variant.getColor() != null && !variant.getColor().trim().isEmpty()) {
            if (name.length() > 0) {
                name.append(" - ");
            }
            name.append(variant.getColor().trim());
        }

        return name.length() > 0 ? name.toString() : "Default";
    }

    /**
     * Generate SKU for product
     */
    private String generateSku(Product product) {
        String brand = product.getBrand() != null ? product.getBrand().toUpperCase().replaceAll("[^A-Z0-9]", "") : "UNK";
        return String.format("%s-PROD-%d", brand, System.currentTimeMillis() % 100000);
    }

    /**
     * Generate SKU for variant
     */
    private String generateVariantSku(ProductVariant variant) {
        String storage = variant.getStorage() != null ? variant.getStorage().replaceAll("[^A-Z0-9]", "") : "";
        String colorCleaned = variant.getColor() != null ? variant.getColor().toUpperCase().replaceAll("[^A-Z0-9]", "") : "";
        String color = colorCleaned.substring(0, Math.min(3, colorCleaned.length()));
        return String.format("VAR-%s-%s-%d", storage, color, System.currentTimeMillis() % 100000);
    }

    /**
     * Parse price string to Double
     */
    private Double parsePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return 0.0;
        }

        try {
            // Remove currency symbols and spaces
            String cleaned = priceStr.replaceAll("[^0-9.,]", "");
            // Replace comma with dot for decimal
            cleaned = cleaned.replace(',', '.');
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            logger.warn("Could not parse price: {}", priceStr);
            return 0.0;
        }
    }
}
