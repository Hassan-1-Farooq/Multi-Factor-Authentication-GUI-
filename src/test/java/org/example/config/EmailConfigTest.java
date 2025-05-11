

package org.example.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class EmailConfigTest {

    @TempDir
    static Path tempDir;

    public static class TestableEmailConfig extends EmailConfig {
        private final InputStream mockInputStream;
        private boolean throwIOException = false;

        public TestableEmailConfig(String propertiesContent) {
            this.mockInputStream = new ByteArrayInputStream(propertiesContent.getBytes(StandardCharsets.UTF_8));
        }

        public TestableEmailConfig setThrowIOException(boolean throwIOException) {
            this.throwIOException = throwIOException;
            return this;
        }

        @Override
        protected InputStream getConfigInputStream() throws IOException {
            if (throwIOException) {
                throw new IOException("Simulated IO error");
            }
            return mockInputStream;
        }
    }


    @BeforeEach
    public void resetStaticFields() {
        // Reset static fields before each test
        EmailConfig.correctEmail = null;
        EmailConfig.correctPassword = null;
        EmailConfig.senderEmail = null;
        EmailConfig.senderPassword = null;
    }

    @Test
    void testCorrectEmail() {
        EmailConfig.correctEmail = "test@example.com";
        assertEquals("test@example.com", EmailConfig.getCorrectEmail());
    }

    @Test
    void testCorrectPassword() {
        EmailConfig.correctPassword = "test123";
        assertEquals("test123", EmailConfig.getCorrectPassword());
    }

    @Test
    void testSenderEmail() {
        EmailConfig.senderEmail = "sender@example.com";
        assertEquals("sender@example.com", EmailConfig.getSenderEmail());
    }

    @Test
    void testSenderPassword() {
        EmailConfig.senderPassword = "sender123";
        assertEquals("sender123", EmailConfig.getSenderPassword());
    }


    @Test
    void testValidatePropertyExists_MissingProperty() {
        Properties props = new Properties();
        props.setProperty("existing_key", "value");

        Exception exception = assertThrows(IllegalStateException.class,
                () -> EmailConfig.validatePropertyExists(props, "missing_key"));

        assertEquals("Required property 'missing_key' is missing", exception.getMessage());
    }

    @Test
    void testValidatePropertyExists_EmptyProperty() {
        Properties props = new Properties();
        props.setProperty("empty_key", "");
        assertDoesNotThrow(() -> EmailConfig.validatePropertyExists(props, "empty_key"));
    }

    @Test
    void testGetRequiredProperty_EmptyValue() {
        Properties props = new Properties();
        props.setProperty("test_key", "");

        Exception exception = assertThrows(IllegalStateException.class,
                () -> EmailConfig.getRequiredProperty(props, "test_key"));

        assertEquals("Required property 'test_key' is missing or empty", exception.getMessage());
    }

    @Test
    void testGetRequiredProperty_MissingProperty() {
        Properties props = new Properties();

        Exception exception = assertThrows(IllegalStateException.class,
                () -> EmailConfig.getRequiredProperty(props, "missing_key"));

        assertEquals("Required property 'missing_key' is missing or empty", exception.getMessage());
    }

    @Test
    void testValidateRequiredProperties_MissingRequired() {
        Properties props = new Properties();
        props.setProperty("email", "test@example.com");

        Exception exception = assertThrows(IllegalStateException.class,
                () -> EmailConfig.validateRequiredProperties(props, true));

        assertTrue(exception.getMessage().contains("password"));
    }

    // for mutation:
    @Test
    void testGetRequiredProperty_ValidValue() {
        Properties props = new Properties();
        props.setProperty("test_key", "some_value");
        String value = EmailConfig.getRequiredProperty(props, "test_key");
        assertEquals("some_value", value);
    }

    @Test
    void testValidatePropertyExists_NullProperties() {
        assertThrows(NullPointerException.class,
                () -> EmailConfig.validatePropertyExists(null, "key"));
    }

    @Test
    void testGetRequiredProperty_WhitespaceValue() {
        Properties props = new Properties();
        props.setProperty("test_key", "   ");

        Exception exception = assertThrows(IllegalStateException.class,
                () -> EmailConfig.getRequiredProperty(props, "test_key"));

        assertEquals("Required property 'test_key' is missing or empty", exception.getMessage());
    }

    @Test
    void testValidateRequiredProperties_MissingEmailOnly() {
        Properties props = new Properties();
        props.setProperty("password", "pass123");

        Exception exception = assertThrows(IllegalStateException.class, () ->
                EmailConfig.validateRequiredProperties(props, true)
        );
        assertTrue(exception.getMessage().contains("email"));
    }

    @Test
    void testGetRequiredProperty_WithValidValue() {
        Properties props = new Properties();
        props.setProperty("key", "value");
        String result = EmailConfig.getRequiredProperty(props, "key");
        assertEquals("value", result);
    }

    @Test
    void testValidateRequiredProperties_AllMissing() {
        Properties props = new Properties();
        assertThrows(IllegalStateException.class,
                () -> EmailConfig.validateRequiredProperties(props, true));
    }

    @Test
    void testGetRequiredProperty_WhitespaceValue2() {
        Properties props = new Properties();
        props.setProperty("test", "   ");
        assertThrows(IllegalStateException.class,
                () -> EmailConfig.getRequiredProperty(props, "test"));
    }

    @Test
    void testLoadProperties_ValidateRequiredPropertiesCalled() {
        Properties props = new Properties();
        props.setProperty("sender_email", "test@example.com");
        props.setProperty("sender_password", "pass123");

        // Should not throw when loadAllProperties is false
        assertDoesNotThrow(() -> EmailConfig.validateRequiredProperties(props, false));

        // Should throw when loadAllProperties is true and required props are missing
        assertThrows(IllegalStateException.class,
                () -> EmailConfig.validateRequiredProperties(props, true));
    }

    @Test
    void testLoadProperties_GetRequiredPropertyCalledForSenderCredentials() {
        Properties props = new Properties();
        props.setProperty("sender_email", "test@example.com");
        props.setProperty("sender_password", "pass123");

        // Verify getRequiredProperty is called for sender credentials
        assertDoesNotThrow(() -> {
            EmailConfig.senderEmail = EmailConfig.getRequiredProperty(props, "sender_email");
            EmailConfig.senderPassword = EmailConfig.getRequiredProperty(props, "sender_password");
        });

        assertEquals("test@example.com", EmailConfig.senderEmail);
        assertEquals("pass123", EmailConfig.senderPassword);
    }

    @Test
    void testLoadProperties_GetRequiredPropertyNotCalledForAuthWhenLoadAllFalse() {
        Properties props = new Properties();
        props.setProperty("sender_email", "test@example.com");
        props.setProperty("sender_password", "pass123");

        // Should not try to get auth properties when loadAllProperties is false
        EmailConfig.validateRequiredProperties(props, false);
        EmailConfig.loadProperties(false);

        assertNull(EmailConfig.correctEmail);
        assertNull(EmailConfig.correctPassword);
    }

    @Test
    void testValidatePropertyExists_PropertyExistsButEmpty() {
        Properties props = new Properties();
        props.setProperty("test_key", "");

        // Should not throw for empty value (only checks existence)
        assertDoesNotThrow(() -> EmailConfig.validatePropertyExists(props, "test_key"));
    }

    @Test
    void testValidateRequiredProperties_ValidatePropertyExistsCalledForAll() {
        Properties props = new Properties();
        props.setProperty("email", "test@example.com");
        props.setProperty("password", "pass123");
        props.setProperty("sender_email", "sender@example.com");
        props.setProperty("sender_password", "sender123");

        // Should validate all properties when loadAllProperties is true
        assertDoesNotThrow(() -> EmailConfig.validateRequiredProperties(props, true));
    }

    @Test
    void testGetRequiredProperty_TrimsWhitespace() {
        Properties props = new Properties();
        props.setProperty("test_key", "  value  ");

        String result = EmailConfig.getRequiredProperty(props, "test_key");
        assertEquals("value", result);
    }

    @Test
    void testLoadProperties_AllPropertiesMissing() {
        Properties props = new Properties();
        assertThrows(IllegalStateException.class,
                () -> EmailConfig.validateRequiredProperties(props, true));
    }

    @Test
    void testGetRequiredProperty_NullAfterTrim() {
        Properties props = new Properties();
        props.setProperty("test_key", "   ");
        assertThrows(IllegalStateException.class,
                () -> EmailConfig.getRequiredProperty(props, "test_key"));
    }


}


