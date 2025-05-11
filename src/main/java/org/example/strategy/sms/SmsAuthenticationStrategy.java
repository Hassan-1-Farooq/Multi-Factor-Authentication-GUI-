package org.example.strategy.sms;

import org.example.exception.AuthenticationException;
import org.example.service.SmsService;
import org.example.strategy.AuthenticationStrategy;

/**
 * Authentication strategy implementation using SMS verification.
 */
public class SmsAuthenticationStrategy implements AuthenticationStrategy {
    private final SmsService smsService;  // Service for sending SMS
    String verificationCode;              // Generated verification code

    /**
     * Constructs strategy with SMS service dependency.
     * @param smsService The SMS service to use
     */
    public SmsAuthenticationStrategy(SmsService smsService) {
        this.smsService = smsService;
    }

    /**
     * Authenticates user by comparing input to verification code.
     * @param userInput The code entered by user
     * @param password Not used in this strategy
     * @return true if codes match, false otherwise
     */
    @Override
    public boolean authenticate(String userInput, String password) {
        return userInput.equals(verificationCode);
    }

    /**
     * Sends verification code via SMS.
     * @param recipient The recipient's phone number
     * @throws AuthenticationException if sending fails
     */
    @Override
    public void sendVerificationCode(String recipient) throws AuthenticationException {
        verificationCode = generateVerificationCode();
//        boolean smsSent = smsService.sendSms(recipient, "Your verification code is: " + verificationCode, anyString());
        smsService.sendSms(recipient, "Your verification code is: " + verificationCode, "contextInfo");

//        if (!smsSent) {
//            throw new AuthenticationException("Failed to send verification code via SMS.");
//        }
    }

    /**
     * Placeholder verification method.
     */
    @Override
    public Object verify(String number) {
        return null;
    }

    /**
     * Generates a random 6-digit verification code.
     * @return The generated code as string
     */
    public String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}