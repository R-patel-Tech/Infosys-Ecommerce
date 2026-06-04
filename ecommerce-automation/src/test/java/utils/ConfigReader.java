package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class ConfigReader {
    private static final String RESOURCE_NAME = "config.properties";
    private static ConfigReader instance;
    private final Properties properties = new Properties();

    private ConfigReader() {
        loadProperties();
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            if (inputStream != null) {
                properties.load(inputStream);
                return;
            }
        } catch (IOException ioException) {
            throw new UncheckedIOException("Unable to read " + RESOURCE_NAME, ioException);
        }

        Path fallbackPath = Paths.get("src", "test", "resources", RESOURCE_NAME).toAbsolutePath().normalize();
        if (Files.exists(fallbackPath)) {
            try (InputStream fileStream = Files.newInputStream(fallbackPath)) {
                properties.load(fileStream);
                return;
            } catch (IOException ioException) {
                throw new UncheckedIOException("Unable to read config from " + fallbackPath, ioException);
            }
        }

        throw new IllegalStateException("config.properties was not found on the classpath or file system.");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException numberFormatException) {
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value.trim());
    }
}
