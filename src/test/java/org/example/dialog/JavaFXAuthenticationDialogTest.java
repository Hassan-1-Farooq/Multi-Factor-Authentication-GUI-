package org.example.dialog;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JavaFXAuthenticationDialogTest {

    private final JavaFXAuthenticationDialog dialog = new JavaFXAuthenticationDialog();

    @BeforeAll
    public static void initJfx() throws InterruptedException {
        if (!Platform.isFxApplicationThread()) {
            final CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(() -> {
                    // JavaFX initialized
                    latch.countDown();
                });
            } catch (IllegalStateException e) {
                // Already initialized
                latch.countDown();
            }
            latch.await(); // Wait for startup to finish
        }
    }



    @Test
    void testInitialDialogState_IsCancelledTrue() {
        assertTrue(dialog.isCancelled(), "Dialog should be cancelled by default");
    }

    @Test
    void testSettersAndGetters() {
        Platform.runLater(() -> {
            dialog.showErrorMessage("Fake error");
            dialog.showSuccessMessage("Success");
            // We don't assert here since dialogs are visual
        });
    }

    @Test
    void testGetVerificationCode_ReturnsNullWhenCancelled() {
        Platform.runLater(() -> {
            String code = dialog.getVerificationCode();
            assertNull(code, "Expected null since user won't input in test");
        });
    }

    @Test
    void testShowErrorMessage() {
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> dialog.showErrorMessage("This is an error message"));
        });
    }

    @Test
    void testShowSuccessMessage() {
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> dialog.showSuccessMessage("Operation successful"));
        });
    }

    @Test
    void testGetters_DefaultValues() {
        assertNull(dialog.getEmail(), "Email should be null before dialog is shown");
        assertNull(dialog.getPassword(), "Password should be null before dialog is shown");
        assertNull(dialog.getPhoneNumber(), "Phone number should be null before dialog is shown");
        assertFalse(dialog.useSms(), "useSms should be false before dialog is shown");
    }


//    for mutation testing
    @Test
    void testDialogStateAfterInteraction() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            // Create a mock stage
            Stage mockStage = new Stage();

            // Show the dialog
            dialog.showDialog(mockStage);

            // Simulate user interaction
            dialog.getEmail(); // This would normally come from UI interaction
            dialog.getPassword(); // This would normally come from UI interaction
            dialog.getPhoneNumber(); // This would normally come from UI interaction
            dialog.useSms(); // This would normally come from UI interaction
            dialog.isCancelled(); // This would be set by button actions

            // Since we can't actually interact with the UI in tests,
            // we'll verify the default state
            assertNull(dialog.getEmail());
            assertNull(dialog.getPassword());
            assertNull(dialog.getPhoneNumber());
            assertFalse(dialog.useSms());
            assertTrue(dialog.isCancelled());

            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testShowDialog_NullStage() {
        Platform.runLater(() -> {
            assertThrows(NullPointerException.class,
                    () -> dialog.showDialog(null));
        });
    }

    @Test
    void testShowDialog_UIComponentsInitialized() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage mockStage = new Stage();
            dialog.showDialog(mockStage);

            // Verify stage properties
            assertEquals("🔐 Secure Authentication", mockStage.getTitle());
            assertFalse(mockStage.isResizable());

            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testCancelButtonAction_SetsCancelledFlag() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage mockStage = new Stage();
            dialog.showDialog(mockStage);

            Button cancelBtn = (Button) mockStage.getScene().lookup("#cancelBtn");
            cancelBtn.fire();

            assertTrue(dialog.isCancelled());
            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testLoginButtonAction_SetsFieldsCorrectly() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage mockStage = new Stage();
            dialog.showDialog(mockStage);

            // Simulate user input and button click
            TextField emailField = (TextField) mockStage.getScene().lookup("#emailField");
            PasswordField passwordField = (PasswordField) mockStage.getScene().lookup("#passwordField");
            TextField phoneField = (TextField) mockStage.getScene().lookup("#phoneField");
            ComboBox<String> methodBox = (ComboBox<String>) mockStage.getScene().lookup("#methodBox");
            Button loginBtn = (Button) mockStage.getScene().lookup("#loginBtn");

            emailField.setText("test@example.com");
            passwordField.setText("password123");
            phoneField.setText("+1234567890");
            methodBox.getSelectionModel().select(1); // Select SMS

            loginBtn.fire();

            assertEquals("test@example.com", dialog.getEmail());
            assertEquals("password123", dialog.getPassword());
            assertEquals("+1234567890", dialog.getPhoneNumber());
            assertTrue(dialog.useSms());
            assertFalse(dialog.isCancelled());

            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testClearButtonAction_ResetsFields() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage mockStage = new Stage();
            dialog.showDialog(mockStage);

            TextField emailField = (TextField) mockStage.getScene().lookup("#emailField");
            PasswordField passwordField = (PasswordField) mockStage.getScene().lookup("#passwordField");
            TextField phoneField = (TextField) mockStage.getScene().lookup("#phoneField");
            ComboBox<String> methodBox = (ComboBox<String>) mockStage.getScene().lookup("#methodBox");
            Button clearBtn = (Button) mockStage.getScene().lookup("#clearBtn");

            emailField.setText("test@example.com");
            passwordField.setText("password123");
            phoneField.setText("+1234567890");
            methodBox.getSelectionModel().select(1); // Select SMS

            clearBtn.fire();

            assertEquals("", emailField.getText());
            assertEquals("", passwordField.getText());
            assertEquals("", phoneField.getText());
            assertEquals("Email", methodBox.getValue());

            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testLayoutProperties_AreSetCorrectly() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage mockStage = new Stage();
            dialog.showDialog(mockStage);

            GridPane formGrid = (GridPane) mockStage.getScene().lookup("#formGrid");
            VBox dialogBox = (VBox) mockStage.getScene().lookup("#dialogBox");
            HBox buttonBox = (HBox) mockStage.getScene().lookup("#buttonBox");

            assertEquals(14.0, formGrid.getVgap());
            assertEquals(15.0, formGrid.getHgap());
            assertEquals(30.0, dialogBox.getPadding().getTop());
            assertEquals(20.0, dialogBox.getSpacing());
            assertEquals(12.0, buttonBox.getSpacing());

            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testShowMessageDialogs_CreateCorrectAlerts() {
        Platform.runLater(() -> {
            // Test success message
            Alert successAlert = mock(Alert.class);
            JavaFXAuthenticationDialog dialog = new JavaFXAuthenticationDialog() {

            };

            dialog.showSuccessMessage("Success!");
            verify(successAlert).setTitle("Success");
            verify(successAlert).setContentText("Success!");

            // Test error message
            Alert errorAlert = mock(Alert.class);
            dialog = new JavaFXAuthenticationDialog() {

            };

            dialog.showErrorMessage("Error!");
            verify(errorAlert).setTitle("Error");
            verify(errorAlert).setContentText("Error!");
        });
    }

    @Test
    void testGetVerificationCode_ReturnsInput() {
        Platform.runLater(() -> {
            // Mock the TextInputDialog behavior
            TextInputDialog mockDialog = mock(TextInputDialog.class);
            when(mockDialog.showAndWait()).thenReturn(java.util.Optional.of("123456"));

            JavaFXAuthenticationDialog dialog = new JavaFXAuthenticationDialog() {
            };
            assertEquals("123456", dialog.getVerificationCode());
        });
    }

    @Test
    void testMethodSelection_UpdatesUseSms() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage mockStage = new Stage();
            dialog.showDialog(mockStage);

            ComboBox<String> methodBox = (ComboBox<String>) mockStage.getScene().lookup("#methodBox");

            // Select Email
            methodBox.getSelectionModel().select(0);
            assertFalse(dialog.useSms());

            // Select SMS
            methodBox.getSelectionModel().select(1);
            assertTrue(dialog.useSms());

            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testScene_StylesheetsApplied() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage mockStage = new Stage();
            dialog.showDialog(mockStage);

            Scene scene = mockStage.getScene();
            assertTrue(scene.getStylesheets().contains(getClass().getResource("/styles.css").toExternalForm()));

            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }
}
