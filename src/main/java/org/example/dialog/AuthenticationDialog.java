package org.example.dialog;

/**
 * Interface for authentication dialog UI.
 */
public interface AuthenticationDialog {
    // Getter methods for user input
    String getEmail();
    String getPassword();
    String getPhoneNumber();

    // Authentication method selection
    boolean useSms();

    // Dialog state
    boolean isCancelled();

    // Verification code input
    String getVerificationCode();

    // UI feedback methods
    void showSuccessMessage(String message);
    void showErrorMessage(String message);
}
