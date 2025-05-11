package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.controller.AuthenticationController;
import org.example.exception.AuthenticationException;
import org.example.dialog.JavaFXAuthenticationDialog;

public class Demo extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            JavaFXAuthenticationDialog dialog = new JavaFXAuthenticationDialog();
            dialog.showDialog(primaryStage);

            AuthenticationController controller = new AuthenticationController(dialog);

            if (controller.authenticate()) {
                dialog.showSuccessMessage("Successfully logged in!");
            } else if (!dialog.isCancelled()) {
                dialog.showErrorMessage("Authentication failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Application error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Explicitly launch JavaFX
        Application.launch(Demo.class, args);
    }


    public boolean runAuthenticationProcess(JavaFXAuthenticationDialog dialog) {
        if (dialog == null) {
            throw new IllegalArgumentException("Dialog cannot be null");
        }

        try {
            AuthenticationController controller = new AuthenticationController(dialog);
            if (dialog.isCancelled()) {
                return false;
            }

            boolean authenticated = controller.authenticate();

            if (authenticated) {
                dialog.showSuccessMessage("Authentication successful");
            } else if (!dialog.isCancelled()) {
                dialog.showErrorMessage("Authentication failed");
            }

            return authenticated;
        } catch (AuthenticationException e) {
            dialog.showErrorMessage("Authentication error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            dialog.showErrorMessage("An unexpected error occurred");
            return false;
        }
    }


    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        // E.164 format: + followed by country code (1-3 digits) and subscriber number (max 12 digits)
        return phoneNumber.matches("^\\+[1-9]\\d{1,14}$");
    }


    // For testing purposes
    protected AuthenticationController createController(JavaFXAuthenticationDialog dialog) {
        return new AuthenticationController(dialog);
    }
}