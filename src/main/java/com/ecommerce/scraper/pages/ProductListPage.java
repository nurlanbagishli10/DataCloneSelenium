package com.ecommerce.scraper.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ProductListPage {

    private static final Logger logger = LoggerFactory.getLogger(ProductListPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private static final String PRODUCT_LINK_XPATH_TEMPLATE =
            "/html/body/div/section/div[2]/div/div[%d]/div[2]/a";

    private static final By NEXT_BUTTON =
            By.xpath("//a[@class='next page-numbers' and contains(text(), 'Sonrakı')]");

    private static final int MAX_PRODUCTS_PER_PAGE = 28;

    // Constructor
    public ProductListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Səhifədəki bütün məhsul linklərini topla
     */
    public List<String> getProductLinks() {
        List<String> productLinks = new ArrayList<>();

        for (int i = 2; i <= MAX_PRODUCTS_PER_PAGE + 1; i++) {
            try {
                String xpath = String.format(PRODUCT_LINK_XPATH_TEMPLATE, i);
                WebElement productLink = wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))
                );

                String href = productLink.getAttribute("href");
                if (href != null && !href.isEmpty()) {
                    productLinks.add(href);
                }

            } catch (Exception e) {
                // Əgər element tapılmasa, loop-u davam etdir
                logger.debug("Məhsul #{} tapılmadı, davam edirik", i);
                break;
            }
        }

        logger.info("Səhifədə {} ədəd məhsul tapıldı", productLinks.size());
        return productLinks;
    }

    /**
     * Next düyməsinə klik et
     */
    public boolean clickNextPage() {
        try {
            WebElement nextButton = wait.until(
                    ExpectedConditions.elementToBeClickable(NEXT_BUTTON)
            );

            nextButton.click();

            // Səhifə yüklənməsini gözlə
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(String.format(PRODUCT_LINK_XPATH_TEMPLATE, 2))
            ));

            logger.info("Növbəti səhifəyə keçid edildi");
            return true;

        } catch (Exception e) {
            logger.warn("Next düyməsi tapılmadı və ya klik edilmədi: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Hazırki səhifə nömrəsini əldə et
     */
    public int getCurrentPageNumber() {
        try {
            WebElement currentPageElement = driver.findElement(
                    By.xpath("//span[@aria-current='page']")
            );
            return Integer.parseInt(currentPageElement.getText().trim());
        } catch (Exception e) {
            logger.debug("Səhifə nömrəsi tapılmadı, default 1 qaytarılır");
            return 1;
        }
    }
}