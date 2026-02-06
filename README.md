# Qiymeti.net E-commerce Scraper

A Selenium-based web scraper for extracting product information from qiymeti.net with database schema mapping support.

## Features

- **Web Scraping**: Automated data extraction from qiymeti.net using Selenium WebDriver
- **Product Data**: Extracts brand, title, variants (storage, color, price), and detailed specifications
- **Database Mapping**: Transforms scraped data into structured database entities ready for import
- **Dual Output**: Generates both raw scraped data and database-formatted JSON

## Output Files

When running the scraper, two JSON files are generated in the `output/` directory:

1. **scraped_raw_YYYYMMDD_HHMMSS.json**: Original scraped data in raw format
2. **database_import_YYYYMMDD_HHMMSS.json**: Database-ready format with entities:
   - Brands (with auto-generated slugs and keywords)
   - Products (with descriptions built from specifications)
   - Product Variants (storage/color combinations)
   - Product Attributes (specifications, variant options, seller info)

## Database Schema

The database output contains the following entities:

### BrandEntity
- id, name, slug, description, is_active, keywords, timestamps

### ProductEntity  
- id, name, slug, description, short_description, sku, price
- brand_id, stock_status, status, visibility, keywords, timestamps

### ProductVariantEntity
- id, product_id, variant_name, sku, price, slug
- stock_status, is_active, is_default, keywords, timestamps

### ProductAttributeEntity
- id, product_id, variant_id, attribute_name, attribute_value
- attribute_type (variant/specification/additional/seller_info)
- attribute_unit, is_visible, sort_order, timestamps

## Usage

### Running the Scraper

```bash
# With browser visible
mvn clean compile exec:java -Dexec.mainClass="com.ecommerce.scraper.QiymetiScraper"

# Headless mode
mvn clean compile exec:java -Dexec.mainClass="com.ecommerce.scraper.QiymetiScraper" -Dexec.args="headless"
```

### Testing the Mapper

```bash
# Compile and run mapper test
mvn test-compile
java -cp target/classes:target/test-classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) com.ecommerce.scraper.mapper.DatabaseMapperTest

# Test with real scraped data
java -cp target/classes:target/test-classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) com.ecommerce.scraper.mapper.RealDataMapperTest
```

## Project Structure

```
src/main/java/com/ecommerce/scraper/
├── model/
│   ├── Product.java                    # Raw product data model
│   ├── ProductVariant.java             # Variant data (storage, color, price)
│   ├── ProductSpecifications.java      # Technical specifications
│   └── db/                             # Database entity models
│       ├── BrandEntity.java
│       ├── ProductEntity.java
│       ├── ProductVariantEntity.java
│       └── ProductAttributeEntity.java
├── mapper/
│   ├── DatabaseMapper.java             # Mapping logic
│   └── DatabaseOutput.java             # Container for mapped entities
├── pages/
│   ├── ProductListPage.java            # List page scraper
│   └── ProductDetailPage.java          # Detail page scraper
├── utils/
│   ├── WebDriverFactory.java           # WebDriver setup
│   └── JsonExporter.java               # JSON export utilities
└── QiymetiScraper.java                 # Main scraper application
```

## Dependencies

- Selenium WebDriver 4.18.1
- Jackson 2.16.1 (JSON processing + JavaTimeModule)
- WebDriverManager 5.6.3
- SLF4J 2.0.12
- Apache Commons Lang3 3.14.0

## Key Features of Database Mapper

- **Brand Deduplication**: Uses HashMap cache to avoid duplicate brands
- **Slug Generation**: Auto-generates URL-friendly slugs from names
- **Keyword Extraction**: Creates searchable keywords from brand and product names
- **Description Building**: Constructs product descriptions from key specifications
- **Variant Naming**: Combines storage + color into readable variant names
- **Attribute Categorization**: Classifies attributes as variant/specification/additional/seller_info
- **Thread-Safe ID Generation**: Uses AtomicLong for unique ID generation
- **LocalDateTime Support**: Proper timestamp handling with ISO-8601 format

## Sample Output

```json
{
  "brands": [{
    "id": 1,
    "name": "Apple",
    "slug": "apple",
    "is_active": true,
    "keywords": "apple",
    "created_at": "2026-02-06T15:30:00"
  }],
  "products": [{
    "id": 1,
    "name": "iPhone 11 xüsusiyyətləri",
    "slug": "iphone-11-xususiyyetleri",
    "description": "Processor: Apple A13 Bionic. RAM: 4 GB...",
    "brand_id": 1,
    "price": 1149.00,
    "keywords": "apple, iphone, 11"
  }],
  "product_variants": [{
    "id": 1,
    "product_id": 1,
    "variant_name": "64 GB - Black",
    "price": 1149.00,
    "is_default": true
  }],
  "product_attributes": [{
    "id": 1,
    "product_id": 1,
    "variant_id": 1,
    "attribute_name": "storage",
    "attribute_value": "64 GB",
    "attribute_type": "variant"
  }]
}
```

## License

This project is for educational purposes.
