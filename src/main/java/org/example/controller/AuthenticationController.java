package org.example.controller;

import org.example.config.EmailConfig;
import org.example.context.AuthenticationContext;
import org.example.dialog.AuthenticationDialog;
import org.example.exception.AuthenticationException;
import org.example.exception.InvalidInputException;
import org.example.factory.AuthenticationStrategyFactory;
import org.example.factory.EmailAuthenticationStrategyFactory;
import org.example.factory.SmsAuthenticationStrategyFactory;
import org.example.util.LoggerUtil;

/**
 * Controller class that manages the authentication process.
 * Coordinates between the dialog UI, authentication strategies, and validation.
 */
public class AuthenticationController {
    private static final int MAX_ATTEMPTS = 3;  // Maximum verification attempts
    private final AuthenticationDialog dialog;  // UI dialog interface

    /**
     * Constructs an AuthenticationController with the given dialog.
     * @param dialog The authentication dialog implementation
     * @throws IllegalArgumentException if dialog is null
     */
    public AuthenticationController(AuthenticationDialog dialog) {
        if (dialog == null) {
            throw new IllegalArgumentException("AuthenticationDialog cannot be null");
        }
        this.dialog = dialog;
    }

    /**
     * Performs the complete authentication process.
     * @return true if authentication succeeds, false otherwise
     * @throws AuthenticationException if authentication fails
     */
    public boolean authenticate() throws AuthenticationException {
        try {
            if (dialog.isCancelled()) {
                return false;
            }

            validateCredentials();
            return performAuthentication();
        } catch (InvalidInputException e) {
            LoggerUtil.logError("Authentication input validation failed", e);
            dialog.showErrorMessage(e.getMessage());
            return false;
        }
    }

    /**
     * Validates user credentials from the dialog.
     * @throws InvalidInputException if credentials are invalid
     */
    void validateCredentials() throws InvalidInputException {
        String email = dialog.getEmail();
        String password = dialog.getPassword();
        String phone = dialog.getPhoneNumber();
        boolean useSms = dialog.useSms();

        if (!email.equals(EmailConfig.getCorrectEmail())) {
            throw new InvalidInputException("Invalid email address");
        }

        if (!password.equals(EmailConfig.getCorrectPassword())) {
            throw new InvalidInputException("Invalid password");
        }

        if (useSms && !isValidPhoneNumber(phone)) {
            throw new InvalidInputException("Invalid phone number format");
        }
    }

    /**
     * Performs authentication using the selected strategy.
     * @return true if authentication succeeds
     * @throws AuthenticationException if authentication fails
     */
    boolean performAuthentication() throws AuthenticationException {
        boolean useSms = dialog.useSms();
        AuthenticationStrategyFactory factory = createStrategyFactory(useSms);

        AuthenticationContext context = new AuthenticationContext();
        context.setStrategy(factory.createStrategy());

        String recipient = useSms ? dialog.getPhoneNumber() : dialog.getEmail();
        context.sendVerificationCode(recipient);

        return verifyCode(context);
    }

    /**
     * Creates the appropriate strategy factory based on authentication method.
     * @param useSms true to use SMS, false for email
     * @return AuthenticationStrategyFactory instance
     */
    AuthenticationStrategyFactory createStrategyFactory(boolean useSms) {
        return useSms
                ? new SmsAuthenticationStrategyFactory()
                : new EmailAuthenticationStrategyFactory() {
            @Override
            protected EmailConfig getEmailConfig() {
                return null;
            }
        };
    }

    /**
     * Verifies the user's verification code with retry logic.
     * @param context The authentication context
     * @return true if verification succeeds within allowed attempts
     */
    boolean verifyCode(AuthenticationContext context) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String verificationCode = dialog.getVerificationCode();
            if (verificationCode != null && context.verify(verificationCode)) {
                return true;
            }

            dialog.showErrorMessage("Invalid verification code. Attempts remaining: " + (MAX_ATTEMPTS - i - 1));
        }
        return false;
    }

    /**
     * Validates phone number format (E.164 format).
     * @param phoneNumber The phone number to validate
     * @return true if valid, false otherwise
     */
    boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\+[1-9]\\d{1,14}$");
    }
}
