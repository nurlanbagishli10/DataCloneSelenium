package com.ecommerce.scraper.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverFactory {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);

    /**
     * ChromeDriver yaradır (Selenium 4)
     */
    public static WebDriver createChromeDriver(boolean headless) {
        logger.info("ChromeDriver yaradılır... (Headless: {})", headless);

        // WebDriverManager - ChromeDriver avtomatik yüklənir
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Headless mode
        if (headless) {
            options.addArguments("--headless=new");
        }

        // Performance optimizations
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");

        // User agent
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/120.0.0.0 Safari/537.36");

        // Window size
        options.addArguments("--window-size=1920,1080");

        // Preferences
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);

        logger.info("✅ ChromeDriver hazırdır");

        return driver;
    }
}