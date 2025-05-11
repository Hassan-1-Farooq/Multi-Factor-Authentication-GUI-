package org.example.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * JavaFX implementation of the authentication dialog.
 */
public class JavaFXAuthenticationDialog implements AuthenticationDialog {
    private String email;         // User-entered email
    private String password;      // User-entered password
    private String phoneNumber;   // User-entered phone number
    private boolean useSms;       // Whether SMS auth is selected
    private boolean cancelled = true; // Dialog cancellation state

    /**
     * Shows the authentication dialog.
     * @param owner The parent stage
     */
    public void showDialog(Stage owner) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("🔐 Secure Authentication");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(owner);
        dialogStage.setResizable(false);

        // UI controls setup...
        // --- Inputs ---
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("you@example.com");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Label phoneLabel = new Label("Phone (for SMS):");
        TextField phoneField = new TextField();
        phoneField.setPromptText("+1234567890");

        Label methodLabel = new Label("Authentication Method:");
        ComboBox<String> methodBox = new ComboBox<>();
        methodBox.getItems().addAll("Email", "SMS");
        methodBox.getSelectionModel().select(0);

        // --- Buttons ---
        Button loginBtn = new Button("Login");
        Button clearBtn = new Button("Clear");
        Button cancelBtn = new Button("Cancel");

        loginBtn.setDefaultButton(true);
        cancelBtn.setCancelButton(true);

        // --- Layout ---
        GridPane formGrid = new GridPane();
        formGrid.setVgap(14);
        formGrid.setHgap(15);

        formGrid.add(emailLabel, 0, 0);
        formGrid.add(emailField, 1, 0);

        formGrid.add(passwordLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);

        formGrid.add(phoneLabel, 0, 2);
        formGrid.add(phoneField, 1, 2);

        formGrid.add(methodLabel, 0, 3);
        formGrid.add(methodBox, 1, 3);

        HBox buttonBox = new HBox(12, cancelBtn, clearBtn, loginBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox dialogBox = new VBox();
        dialogBox.setPadding(new Insets(30));
        dialogBox.setSpacing(20);
        dialogBox.getChildren().addAll(formGrid, buttonBox);
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.getStyleClass().add("dialog-card");

        StackPane root = new StackPane(dialogBox);
        root.setPadding(new Insets(40, 20, 40, 20));

        Scene scene = new Scene(root, 450, 330);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogStage.setScene(scene);

        loginBtn.setOnAction(e -> {
            email = emailField.getText();
            password = passwordField.getText();
            phoneNumber = phoneField.getText();
            useSms = methodBox.getValue().equals("SMS");
            cancelled = false;
            dialogStage.close();
        });

        cancelBtn.setOnAction(e -> {
            cancelled = true;
            dialogStage.close();
        });

        clearBtn.setOnAction(e -> {
            emailField.clear();
            passwordField.clear();
            phoneField.clear();
            methodBox.getSelectionModel().select(0);
        });

        dialogStage.showAndWait();
    }

    /**
     * Shows verification code input dialog.
     * @return The entered verification code or null if cancelled
     */
    @Override
    public String getVerificationCode() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Verification");
        inputDialog.setHeaderText("Enter verification code:");
        inputDialog.setContentText("Code:");

        return inputDialog.showAndWait().orElse(null);
    }

    /**
     * Shows success message dialog.
     * @param message The message to display
     */
    @Override
    public void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows error message dialog.
     * @param message The message to display
     */
    @Override
    public void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter methods for user input
    @Override public String getEmail() { return email; }
    @Override public String getPassword() { return password; }
    @Override public String getPhoneNumber() { return phoneNumber; }
    @Override public boolean useSms() { return useSms; }
    @Override public boolean isCancelled() { return cancelled; }
}
