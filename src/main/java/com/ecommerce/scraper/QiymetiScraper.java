package com.ecommerce.scraper;

import com.ecommerce.scraper.mapper.DatabaseMapper;
import com.ecommerce.scraper.mapper.DatabaseOutput;
import com.ecommerce.scraper.model.Product;
import com.ecommerce.scraper.pages.ProductDetailPage;
import com.ecommerce.scraper.pages.ProductListPage;
import com.ecommerce.scraper.utils.JsonExporter;
import com.ecommerce.scraper.utils.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QiymetiScraper {

    private static final Logger logger = LoggerFactory.getLogger(QiymetiScraper.class);

    private static final String BASE_URL = "https://qiymeti.net/qiymetleri/telefon/";
    private static final int MAX_PAGES = 1;

    private final WebDriver driver;
    private final JsonExporter jsonExporter;
    private final DatabaseMapper databaseMapper;
    private final List<Product> allProducts;

    public QiymetiScraper(boolean headless) {
        this.driver = WebDriverFactory.createChromeDriver(headless);
        this.jsonExporter = new JsonExporter();
        this.databaseMapper = new DatabaseMapper();
        this.allProducts = new ArrayList<>();
    }

    /**
     * Scraping prosesini başlat
     */
    public void start() {
        logger.info("========================================");
        logger.info("🚀 Qiymeti.net Scraper başladı");
        logger.info("========================================");

        try {
            driver.get(BASE_URL);
            logger.info("Əsas səhifə açıldı: {}", BASE_URL);

            ProductListPage listPage = new ProductListPage(driver);

            int currentPage = 1;

            while (currentPage <= MAX_PAGES) {
                logger.info("📄 Səhifə {}/{} işlənir...", currentPage, MAX_PAGES);

                // Məhsul linklərini topla
                List<String> productLinks = listPage.getProductLinks();

                if (productLinks.isEmpty()) {
                    logger.warn("⚠️ Səhifədə məhsul tapılmadı, dayanır");
                    break;
                }

                // Hər məhsula daxil ol
                for (int i = 0; i < productLinks.size(); i++) {
                    String productUrl = productLinks.get(i);

                    logger.info("  📱 Məhsul {}/{} - {}",
                            i + 1, productLinks.size(), productUrl);

                    try {
                        // Məhsul səhifəsinə keç
                        driver.get(productUrl);

                        ProductDetailPage detailPage = new ProductDetailPage(driver);

                        // Data topla
                        Product product = detailPage.scrapeProductData();
                        allProducts.add(product);

                        logger.info("  ✅ {} - {} variant toplandı",
                                product.getTitle(),
                                product.getVariants().size());

                        // Geri qayıt
                        driver.navigate().back();

                    } catch (Exception e) {
                        logger.error("  ❌ Məhsul scrape xətası: {}", e.getMessage());
                    }
                }

                // Növbəti səhifəyə keç
                if (currentPage < MAX_PAGES) {
                    boolean hasNext = listPage.clickNextPage();
                    if (!hasNext) {
                        logger.info("Son səhifə, scraping bitir");
                        break;
                    }
                }

                currentPage++;
            }

        } catch (Exception e) {
            logger.error("❌ Scraper xətası: {}", e.getMessage(), e);

        } finally {
            cleanup();
        }
    }

    /**
     * Təmizlik və JSON export
     */
    private void cleanup() {
        logger.info("========================================");
        logger.info("📊 Scraping tamamlandı");
        logger.info("Toplam məhsul sayı: {}", allProducts.size());

        // Export JSON files
        if (!allProducts.isEmpty()) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            
            // Export raw scraped data
            String rawOutputFile = String.format("output/scraped_raw_%s.json", timestamp);
            jsonExporter.exportToJson(allProducts, rawOutputFile);

            // Export database formatted data
            String dbOutputFile = String.format("output/database_import_%s.json", timestamp);
            DatabaseOutput dbOutput = databaseMapper.mapToDatabase(allProducts);
            jsonExporter.exportDatabaseOutput(dbOutput, dbOutputFile);
        }

        // WebDriver-ı bağla
        if (driver != null) {
            driver.quit();
            logger.info("✅ WebDriver bağlandı");
        }

        logger.info("========================================");
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        // Headless mode (false = browser görünəcək, true = background)
        boolean headless = false;

        // Args-dan headless parametri
        if (args.length > 0 && args[0].equalsIgnoreCase("headless")) {
            headless = true;
        }

        QiymetiScraper scraper = new QiymetiScraper(headless);
        scraper.start();
    }
}