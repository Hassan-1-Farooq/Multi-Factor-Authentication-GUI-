package org.example.config;

import org.example.util.LoggerUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Abstract configuration class for email-related settings.
 * Loads and manages email configuration properties from a properties file.
 */
public abstract class EmailConfig {
    private static final String CONFIG_FILE = "config.properties";
    static String correctEmail;       // Valid email for authentication
    static String correctPassword;    // Valid password for authentication
    static String senderEmail;        // Email address used to send verification emails
    static String senderPassword;     // Password for the sender email account

    // Static initializer loads properties when class is loaded
    static {
        loadProperties(true);
    }



    /**
     * Constructor that reloads properties when instantiated
     */
    public EmailConfig() {
        loadProperties(false);
    }

    /**
     * Loads properties from the configuration file.
     * @param loadAllProperties If true, loads all properties including correct email/password
     */
    public static void loadProperties(boolean loadAllProperties) {
        Properties properties = new Properties();
        try (InputStream input = EmailConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: " + CONFIG_FILE);
            }
            properties.load(input);

            validateRequiredProperties(properties, loadAllProperties);

            if (loadAllProperties) {
                correctEmail = getRequiredProperty(properties, "email");
                correctPassword = getRequiredProperty(properties, "password");
            }
            senderEmail = getRequiredProperty(properties, "sender_email");
            senderPassword = getRequiredProperty(properties, "sender_password");
        } catch (Exception e) {
            LoggerUtil.logError("Failed to load email configuration", e);
            throw new IllegalStateException("Configuration loading failed", e);
        }
    }

    /**
     * Validates that required properties exist in the properties file
     */
    static void validateRequiredProperties(Properties properties, boolean loadAllProperties) {
        if (loadAllProperties) {
            validatePropertyExists(properties, "email");
            validatePropertyExists(properties, "password");
        }
        validatePropertyExists(properties, "sender_email");
        validatePropertyExists(properties, "sender_password");
    }

    /**
     * Gets a required property value and validates it's not empty
     */
    static String getRequiredProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Required property '" + key + "' is missing or empty");
        }
        return value.trim();
    }

    /**
     * Validates that a property exists in the properties file
     */
    static void validatePropertyExists(Properties properties, String key) {
        if (!properties.containsKey(key)) {
            throw new IllegalStateException("Required property '" + key + "' is missing");
        }
    }

    // Getter methods for configuration values
    public static String getSenderEmail() {
        return senderEmail;
    }

    public static String getSenderPassword() {
        return senderPassword;
    }

    public static String getCorrectEmail() {
        return correctEmail;
    }

    public static String getCorrectPassword() {
        return correctPassword;
    }

    /**
     * Abstract method to be implemented by subclasses to provide config input stream
     */
    protected abstract InputStream getConfigInputStream() throws IOException;
}
