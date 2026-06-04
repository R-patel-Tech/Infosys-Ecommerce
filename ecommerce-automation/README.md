# ecommerce-automation

Selenium + TestNG automation framework for the ecommerce application.

## Tech Stack

- Selenium Java
- TestNG
- WebDriverManager
- Extent Reports
- Apache POI
- Log4j2

## Run

```bash
mvn test
```

## Configuration

Update `src/test/resources/config.properties` for:

- `baseUrl`
- test credentials
- browser mode
- wait timeouts
- report and screenshot locations

## Framework Notes

- Page Object Model is used for maintainability.
- `BaseTest` manages browser lifecycle and suite hooks.
- `ExtentReports` and screenshots are wired through a TestNG listener.
- `ExcelUtil` is included for future data-driven testing.
- The structure is ready to grow into API automation and CI/CD pipelines.
