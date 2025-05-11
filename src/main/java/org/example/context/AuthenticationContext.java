package org.example.context;

import org.example.exception.AuthenticationException;
import org.example.strategy.AuthenticationStrategy;

/**
 * Context class for managing authentication strategies.
 * Implements the Strategy design pattern to allow different authentication methods.
 */
public class AuthenticationContext {
    private AuthenticationStrategy strategy;

    /**
     * Sets the authentication strategy to use.
     * @param strategy The authentication strategy implementation
     */
    public void setStrategy(AuthenticationStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Sends a verification code using the current strategy.
     * @param recipient The recipient's email or phone number
     * @throws AuthenticationException if sending fails
     */
    public void sendVerificationCode(String recipient) throws AuthenticationException {
        strategy.sendVerificationCode(recipient);
    }

    /**
     * Verifies user input against the verification code.
     * @param userInput The code entered by the user
     * @return true if verification succeeds, false otherwise
     */
    public boolean verify(String userInput) {
        return strategy.authenticate(userInput, "password");
    }
}
