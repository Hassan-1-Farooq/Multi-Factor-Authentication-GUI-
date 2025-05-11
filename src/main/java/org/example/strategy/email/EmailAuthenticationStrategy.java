package org.example.strategy.email;

import org.example.exception.AuthenticationException;
import org.example.service.EmailService;
import org.example.strategy.AuthenticationStrategy;

/**
 * Authentication strategy implementation using email verification.
 */
public class EmailAuthenticationStrategy implements AuthenticationStrategy {
    private final EmailService emailService;  // Service for sending emails
    public String verificationCode;                 // Generated verification code

    /**
     * Constructs strategy with email service dependency.
     * @param emailService The email service to use
     */
    public EmailAuthenticationStrategy(EmailService emailService) {
        this.emailService = emailService;
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
     * Sends verification code via email.
     * @param recipient The recipient's email address
     * @throws AuthenticationException if sending fails
     */
    @Override
    public void sendVerificationCode(String recipient) throws AuthenticationException {
        verificationCode = generateVerificationCode();
        boolean emailSent = emailService.sendEmail(recipient, "Your Verification Code", "Your verification code is: " + verificationCode);
        if (!emailSent) {
            throw new AuthenticationException("Failed to send verification code via email.");
        }
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
