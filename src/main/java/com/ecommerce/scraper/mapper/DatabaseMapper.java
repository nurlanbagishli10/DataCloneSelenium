package com.ecommerce.scraper.mapper;

import com.ecommerce.scraper.model.AttributeGroup;
import com.ecommerce.scraper.model.Product;
import com.ecommerce.scraper.model.ProductSpecifications;
import com.ecommerce.scraper.model.ProductVariant;
import com.ecommerce.scraper.model.SpecificationItem;
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

    // Attribute type constants
    private static final String ATTR_TYPE_COLOR = "color";
    private static final String ATTR_TYPE_TEXT = "text";
    private static final String ATTR_KEY_COLORS = "colors";
    private static final String ATTR_KEY_COLOR = "color";

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
            storageAttr.setAttributeKey(null);
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
            colorAttr.setAttributeKey(null);
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
            sellerAttr.setAttributeKey(null);
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
            websiteAttr.setAttributeKey(null);
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
            currencyAttr.setAttributeKey(null);
            currencyAttr.setAttributeType("seller_info");
            output.addProductAttribute(currencyAttr);
        }
    }

    /**
     * Create product attributes from attributes (not specifications)
     */
    public void createProductAttributes(Product product, Long productId, DatabaseOutput output) {
        if (product.getAttributes() == null) {
            return;
        }
        
        int sortOrder = 1;
        
        for (AttributeGroup group : product.getAttributes()) {
            for (SpecificationItem item : group.getItems()) {
                
                ProductAttributeEntity attr = new ProductAttributeEntity();
                attr.setId(attributeIdCounter.getAndIncrement());
                attr.setProductId(productId);
                attr.setVariantId(null);  // Product-level attributes
                
                // Map label and key from scraped_raw
                attr.setAttributeLabel(item.getLabel());  // e.g., "Prosessor"
                attr.setAttributeKey(item.getKey());      // e.g., "processor"
                attr.setAttributeValue(item.getValue());
                attr.setSortOrder(sortOrder++);
                
                // Set attribute type based on key
                if (ATTR_KEY_COLORS.equals(item.getKey()) || ATTR_KEY_COLOR.equals(item.getKey())) {
                    attr.setAttributeType(ATTR_TYPE_COLOR);
                } else {
                    attr.setAttributeType(ATTR_TYPE_TEXT);
                }
                
                output.addProductAttribute(attr);
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
