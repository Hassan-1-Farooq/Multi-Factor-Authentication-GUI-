package org.example.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.Properties;
import java.io.InputStream;

/**
 * Service class for sending SMS messages using Twilio API.
 */
public class SmsService {
    final String accountSid;       // Twilio account SID
    final String authToken;       // Twilio auth token
    final String twilioPhoneNumber; // Twilio phone number

    /**
     * Constructs SmsService and initializes Twilio with credentials from config.
     * @throws RuntimeException if configuration fails to load
     */
    public SmsService() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
            this.accountSid = properties.getProperty("twilio_account_sid");
            this.authToken = properties.getProperty("twilio_auth_token");
            this.twilioPhoneNumber = properties.getProperty("twilio_phone_number");

            // Initialize Twilio API with credentials
            Twilio.init(accountSid, authToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Twilio configuration", e);
        }
    }

    /**
     * Sends an SMS message to the specified phone number.
     *
     * @param s
     * @param to          The recipient's phone number in E.164 format
     * @param message     The message content to send
     * @param contextInfo
     * @return true if SMS sent successfully, false otherwise
     */
    public boolean sendSms(String to, String message, String contextInfo) {
        if (to == null || to.trim().isEmpty()) {
            System.out.println("Recipient phone number is null or empty.");
            return false;
        }
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Message body is null or empty.");
            return false;
        }

        try {
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilioPhoneNumber),
                    message
            ).create();
            System.out.println("SMS sent successfully to " + to);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to send SMS: " + e.getMessage());
            return false;
        }
    }
}