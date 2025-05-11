package org.example.strategy;

import org.example.exception.AuthenticationException;

/**
 * Interface defining authentication strategy contract.
 */
public interface AuthenticationStrategy {
    /**
     * Authenticates user input against verification code.
     * @param userInput The user-provided code
     * @param password The user's password (may be unused)
     * @return true if authentication succeeds
     */
    boolean authenticate(String userInput, String password);

    /**
     * Sends verification code to recipient.
     * @param recipient Email or phone number
     * @throws AuthenticationException if sending fails
     */
    void sendVerificationCode(String recipient) throws AuthenticationException;

    /**
     * Verifies the received authentication number.
     * @param number The number to verify
     * @return Verification status (implementation specific)
     */
    Object verify(String number);
}
