package com.ecommerce.scraper.pages;

import com.ecommerce.scraper.model.Product;
import com.ecommerce.scraper.model.ProductSpecifications;
import com.ecommerce.scraper.model.ProductVariant;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailPage {

    private static final Logger logger = LoggerFactory.getLogger(ProductDetailPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private static final By PRODUCT_TITLE = By.cssSelector("h2.heading");

    private static final By STORAGE_FILTER =
            By.xpath("//div[@class='filter']//span[@class='text' and contains(text(), 'Daxili yaddaş')]");

    private static final By STORAGE_OPTIONS =
            By.xpath("//div[@class='filter']//span[@class='option' and @data-field-key='yaddas']");

    private static final By COLOR_FILTER =
            By.xpath("//div[@class='filter']//span[@class='text' and contains(text(), 'Rəng')]");

    private static final By COLOR_OPTIONS =
            By.xpath("//div[@class='filter']//span[@class='option' and @data-field-key='color']");

    private static final By PRICE_LIST = By.cssSelector("ul.price-list");

    private static final By FIRST_PRICE_ITEM =
            By.cssSelector("ul.price-list li.price-item:first-of-type");

    private static final By SPECIFICATIONS_TABLE =
            By.cssSelector("#specifications table.specifications");

    private static final By PROCESSOR_FIELD = By.id("smartphone_processor");

    // Constructor
    public ProductDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Məhsul məlumatlarını tam olaraq topla
     */
    public Product scrapeProductData() {
        logger.info("Məhsul data toplanması başlayır: {}", driver.getCurrentUrl());

        Product product = new Product();

        // Brand və Title
        extractBrandAndTitle(product);

        // Specifications
        ProductSpecifications specs = extractSpecifications();
        product.setSpecifications(specs);

        // Variantlar
        List<ProductVariant> variants = extractAllVariants();
        product.setVariants(variants);

        logger.info("Məhsul data toplandı: {} - {} variant",
                product.getTitle(), variants.size());

        return product;
    }

    /**
     * Brand və Title çıxarma
     */
    private void extractBrandAndTitle(Product product) {
        try {
            WebElement titleElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(PRODUCT_TITLE)
            );

            String fullTitle = titleElement.getText().trim();
            product.setTitle(fullTitle);

            // Brand - Prosessordan və ya title-dan çıxar
            String brand = extractBrand(fullTitle);
            product.setBrand(brand);

            logger.debug("Brand: {}, Title: {}", brand, fullTitle);

        } catch (Exception e) {
            logger.error("Brand və ya Title çıxarıla bilmədi: {}", e.getMessage());
        }
    }

    /**
     * Brand adını müəyyən et
     */
    private String extractBrand(String fullTitle) {
        try {
            // Əvvəlcə prosessordan yoxla
            WebElement processorElement = driver.findElement(PROCESSOR_FIELD);
            String processorText = processorElement.getText().toLowerCase();

            if (processorText.contains("apple")) {
                return "Apple";
            } else if (processorText.contains("samsung")) {
                return "Samsung";
            } else if (processorText.contains("snapdragon") || processorText.contains("qualcomm")) {
                // Əgər Snapdragon varsa, title-dan brand-ı tap
                return extractBrandFromTitle(fullTitle);
            }
        } catch (Exception e) {
            logger.debug("Prosessordan brand tapılmadı, title-dan çıxarılır");
        }

        // Title-dan ilk sözü götür
        return extractBrandFromTitle(fullTitle);
    }

    /**
     * Title-dan brand çıxar
     */
    private String extractBrandFromTitle(String title) {
        if (title == null || title.isEmpty()) {
            return "Unknown";
        }

        String[] words = title.split("\\s+");
        return words[0];
    }

    /**
     * Bütün variantları topla (storage + color kombinasiyaları)
     */
    private List<ProductVariant> extractAllVariants() {
        List<ProductVariant> allVariants = new ArrayList<>();

        try {
            // Storage seçimlərini əldə et
            List<VariantOption> storageOptions = getVariantOptions(STORAGE_OPTIONS);

            // Color seçimlərini əldə et
            List<VariantOption> colorOptions = getVariantOptions(COLOR_OPTIONS);

            logger.info("Tapılan variantlar - Storage: {}, Color: {}",
                    storageOptions.size(), colorOptions.size());

            // Əgər heç bir variant yoxdursa, sadəcə qiyməti götür
            if (storageOptions.isEmpty() && colorOptions.isEmpty()) {
                ProductVariant variant = extractPriceData();
                if (variant != null) {
                    allVariants.add(variant);
                }
                return allVariants;
            }

            // Əgər yalnız storage varsa
            if (!storageOptions.isEmpty() && colorOptions.isEmpty()) {
                for (VariantOption storage : storageOptions) {
                    clickVariantOption(storage);
                    ProductVariant variant = extractPriceData();
                    if (variant != null) {
                        variant.setStorage(storage.text);
                        allVariants.add(variant);
                    }
                }
                return allVariants;
            }

            // Əgər yalnız color varsa
            if (storageOptions.isEmpty() && !colorOptions.isEmpty()) {
                for (VariantOption color : colorOptions) {
                    clickVariantOption(color);
                    ProductVariant variant = extractPriceData();
                    if (variant != null) {
                        variant.setColor(color.text);
                        allVariants.add(variant);
                    }
                }
                return allVariants;
            }

            // Hər iki variant varsa - bütün kombinasiyaları topla
            for (VariantOption storage : storageOptions) {
                clickVariantOption(storage);
                waitForPriceUpdate();

                for (VariantOption color : colorOptions) {
                    clickVariantOption(color);
                    waitForPriceUpdate();

                    ProductVariant variant = extractPriceData();
                    if (variant != null) {
                        variant.setStorage(storage.text);
                        variant.setColor(color.text);
                        allVariants.add(variant);

                        logger.debug("Variant toplandı: {} - {}", storage.text, color.text);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Variantlar toplanarkən xəta: {}", e.getMessage(), e);
        }

        return allVariants;
    }

    /**
     * Variant seçimlərini əldə et
     */
    private List<VariantOption> getVariantOptions(By locator) {
        List<VariantOption> options = new ArrayList<>();

        try {
            List<WebElement> elements = driver.findElements(locator);

            for (WebElement element : elements) {
                String text = element.getAttribute("data-spec-text");
                String specId = element.getAttribute("data-spec-id");

                if (text != null && !text.isEmpty()) {
                    options.add(new VariantOption(element, text, specId));
                }
            }

        } catch (Exception e) {
            logger.debug("Variant option-lar tapılmadı: {}", e.getMessage());
        }

        return options;
    }

    /**
     * Variant option-a klik et (stale element handling ilə)
     */
    private void clickVariantOption(VariantOption option) {
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            try {
                // Fresh element tap
                WebElement freshElement = wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath(String.format(
                                        "//span[@class='option' and @data-spec-id='%s']",
                                        option.specId
                                ))
                        )
                );

                // JavaScript click (daha etibarlı)
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();", freshElement
                );

                logger.debug("Variant seçildi: {}", option.text);
                return;

            } catch (StaleElementReferenceException e) {
                logger.debug("Stale element, retry {}/{}", i + 1, maxRetries);
                waitForPriceUpdate();
            } catch (Exception e) {
                logger.warn("Variant klik xətası (retry {}/{}): {}",
                        i + 1, maxRetries, e.getMessage());
            }
        }

        logger.error("Variant klik edilə bilmədi: {}", option.text);
    }

    /**
     * Qiymət yenilənməsini gözlə
     */
    private void waitForPriceUpdate() {
        try {
            // Qısa gözləmə - DOM yenilənməsi üçün
            wait.until(ExpectedConditions.presenceOfElementLocated(FIRST_PRICE_ITEM));

            // Əlavə stabillik yoxlaması
            wait.until(driver -> {
                try {
                    driver.findElement(FIRST_PRICE_ITEM);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

        } catch (Exception e) {
            logger.debug("Qiymət yenilənməsi gözləmədi: {}", e.getMessage());
        }
    }

    /**
     * İlk (ən yuxarıdakı) qiymət məlumatını çıxar
     */
    private ProductVariant extractPriceData() {
        try {
            WebElement firstPriceItem = wait.until(
                    ExpectedConditions.presenceOfElementLocated(FIRST_PRICE_ITEM)
            );

            ProductVariant variant = new ProductVariant();

            // Qiymət
            String price = firstPriceItem.findElement(
                    By.cssSelector("meta[itemprop='price']")
            ).getAttribute("content");
            variant.setPrice(price);

            // Valyuta
            String currency = firstPriceItem.findElement(
                    By.cssSelector("meta[itemprop='priceCurrency']")
            ).getAttribute("content");
            variant.setCurrency(currency);

            // Satıcı
            try {
                String seller = firstPriceItem.findElement(
                        By.cssSelector("meta[itemprop='name']")
                ).getAttribute("content");
                variant.setSeller(seller);
            } catch (Exception e) {
                logger.debug("Satıcı məlumatı tapılmadı");
            }

            // Website
            try {
                String website = firstPriceItem.findElement(
                        By.cssSelector(".website")
                ).getText().trim();
                variant.setWebsite(website);
            } catch (Exception e) {
                logger.debug("Website məlumatı tapılmadı");
            }

            return variant;

        } catch (Exception e) {
            logger.error("Qiymət məlumatı çıxarıla bilmədi: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Spesifikasiyaları çıxar
     */
    private ProductSpecifications extractSpecifications() {
        ProductSpecifications specs = new ProductSpecifications();

        try {
            List<WebElement> specTables = driver.findElements(SPECIFICATIONS_TABLE);

            for (WebElement table : specTables) {
                List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));

                for (WebElement row : rows) {
                    try {
                        String fieldId = row.getAttribute("id");
                        String fieldName = row.findElement(By.cssSelector(".field-name"))
                                .getText().trim();
                        String value = row.findElement(By.cssSelector(".values"))
                                .getText().trim();

                        mapSpecificationField(specs, fieldId, fieldName, value);

                    } catch (Exception e) {
                        logger.debug("Spesifikasiya sətri oxuna bilmədi");
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Spesifikasiyalar çıxarıla bilmədi: {}", e.getMessage());
        }

        return specs;
    }

    /**
     * Spesifikasiya field-lərini map et
     */
    private void mapSpecificationField(ProductSpecifications specs,
                                       String fieldId, String fieldName, String value) {
        if (fieldId == null) return;

        switch (fieldId) {
            case "osystem":
                specs.setOperatingSystem(value);
                break;
            case "smartphone_processor":
                specs.setProcessor(value);
                break;
            case "prosessort":
                specs.setMaxProcessorSpeed(value);
                break;
            case "prosessors":
                specs.setProcessorCount(value);
                break;
            case "graphic_processor":
                specs.setGraphicProcessor(value);
                break;
            case "ekrano":
                specs.setScreenSize(value);
                break;
            case "smartphone_screen_type":
                specs.setScreenType(value);
                break;
            case "refresh_rate":
                specs.setRefreshRate(value);
                break;
            case "resolution":
                specs.setResolution(value);
                break;
            case "back_camera_mp":
                specs.setBackCameraMp(value);
                break;
            case "back_camera_count":
                specs.setBackCameraCount(value);
                break;
            case "front_camera_mp":
                specs.setFrontCameraMp(value);
                break;
            case "video_resolution":
                specs.setVideoResolution(value);
                break;
            case "ram":
                specs.setRam(value);
                break;
            case "batareya":
                specs.setBattery(value);
                break;
            case "weight":
                specs.setWeight(value);
                break;
            case "uzunluq":
                specs.getDimensions().put("length", value);
                break;
            case "eni":
                specs.getDimensions().put("width", value);
                break;
            case "qalinliq":
                specs.getDimensions().put("thickness", value);
                break;
            default:
                // Digər spesifikasiyalar
                specs.getAdditionalSpecs().put(fieldName, value);
                break;
        }
    }

    /**
     * Geri qayıt (browser back)
     */
    public void navigateBack() {
        driver.navigate().back();

        // Səhifənin yüklənməsini gözlə
        wait.until(driver ->
                ((JavascriptExecutor) driver).executeScript("return document.readyState")
                        .equals("complete")
        );

        logger.debug("Geri qayıdıldı");
    }

    /**
     * Inner class - Variant option data holder
     */
    private static class VariantOption {
        WebElement element;
        String text;
        String specId;

        VariantOption(WebElement element, String text, String specId) {
            this.element = element;
            this.text = text;
            this.specId = specId;
        }
    }
}