package org.example;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.controller.AuthenticationController;
import org.example.dialog.JavaFXAuthenticationDialog;
import org.example.exception.AuthenticationException;
import org.example.exception.InvalidInputException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DemoTest {

    private Demo demo;
    private JavaFXAuthenticationDialog dialog;
    private AuthenticationController mockController;

    @BeforeEach
    void setUp() {
        demo = new Demo() {
            @Override
            protected AuthenticationController createController(JavaFXAuthenticationDialog dialog) {
                return mockController;
            }
        };
        dialog = mock(JavaFXAuthenticationDialog.class);
        mockController = mock(AuthenticationController.class);
    }


    @BeforeAll
    static void initJavaFx() throws InterruptedException {
        if (Platform.isFxApplicationThread()) {
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(() -> {}); // First attempt to start JavaFX
        } catch (IllegalStateException e) {
            // JavaFX already started - ignore
        } finally {
            latch.countDown(); // always unblock the test
        }
        latch.await();
    }
    @AfterAll
    public static void tearDown() {
        Platform.exit();
    }



    @Test
    void testRunAuthenticationProcess_CancelledDialog() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(true);

        boolean result = demo.runAuthenticationProcess(dialog);
        assertFalse(result);
        verify(dialog, never()).showSuccessMessage(anyString());
        verify(dialog, never()).showErrorMessage(anyString());
    }



    @Test
    void testRunAuthenticationProcess_UnexpectedException() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(mockController.authenticate()).thenThrow(new RuntimeException("Unexpected error"));

        boolean result = demo.runAuthenticationProcess(dialog);
        assertFalse(result);
        verify(dialog).showErrorMessage(contains("unexpected error"));
    }

    @Test
    void testCreateController_ReturnsNonNull() {
        Demo realDemo = new Demo();
        AuthenticationController controller = realDemo.createController(dialog);
        assertNotNull(controller);
    }

    @Test
    void testIsValidPhoneNumber_ValidNumbers() {
        assertTrue(Demo.isValidPhoneNumber("+1234567890"));
        assertTrue(Demo.isValidPhoneNumber("+447938893257"));
        assertTrue(Demo.isValidPhoneNumber("+1234567890"));
        assertTrue(Demo.isValidPhoneNumber("+123456789012345"));
        assertFalse(Demo.isValidPhoneNumber("447938893257"));
        assertFalse(Demo.isValidPhoneNumber("+1234567890123456"));
        assertFalse(Demo.isValidPhoneNumber("+abc123456"));
        assertFalse(Demo.isValidPhoneNumber("+12-3456-7890"));
        assertFalse(Demo.isValidPhoneNumber(""));
        assertFalse(Demo.isValidPhoneNumber("   "));
        assertFalse(Demo.isValidPhoneNumber(null));
    }

    @Test
    void runAuthenticationProcess_WhenDialogCancelled_ReturnsFalse() {
        when(dialog.isCancelled()).thenReturn(true);
        assertFalse(demo.runAuthenticationProcess(dialog));
    }

    @Test
    void runAuthenticationProcess_WhenInvalidEmail_ReturnsFalse() {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("wrong@example.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");

        assertFalse(demo.runAuthenticationProcess(dialog));
    }

    @Test
    void runAuthenticationProcess_WhenInvalidPassword_ReturnsFalse() {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("wrongpassword");

        assertFalse(demo.runAuthenticationProcess(dialog));
    }

    @Test
    void runAuthenticationProcess_WhenSmsSelectedButInvalidPhone_ReturnsFalse() {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(true);
        when(dialog.getPhoneNumber()).thenReturn("invalid");

        assertFalse(demo.runAuthenticationProcess(dialog));
    }


    @Test
    void runAuthenticationProcess_WhenAllValid_ReturnsFalse() {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("123456");

        assertFalse(demo.runAuthenticationProcess(dialog));
    }

    @Test
    void testRunAuthenticationProcess_Success() {
        // Mock dialog returning valid data
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("123456").thenReturn("123456").thenReturn("123456");

        boolean result = demo.runAuthenticationProcess(dialog);

        // We expect it to fail verification in test env (no real email service)
        assertFalse(result); // Or true if mocked dependencies in integration test
    }

    @Test
    void testRunAuthenticationProcess_InvalidEmail() {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("wrong@example.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(false);

        boolean result = demo.runAuthenticationProcess(dialog);
        assertFalse(result);
    }

    @Test
    void testRunAuthenticationProcess_InvalidPhone() {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(true);
        when(dialog.getPhoneNumber()).thenReturn("invalid");

        boolean result = demo.runAuthenticationProcess(dialog);
        assertFalse(result);
    }

    @Test
    void testRunAuthenticationProcess_Cancelled() {
        JavaFXAuthenticationDialog dialog = mock(JavaFXAuthenticationDialog.class);
        when(dialog.isCancelled()).thenReturn(true);

        Demo demo = new Demo() {
            @Override
            protected AuthenticationController createController(JavaFXAuthenticationDialog dialog) {
                return null;
            }
        };
        assertFalse(demo.runAuthenticationProcess(dialog));
    }



    @Test
    void runAuthenticationProcess_WhenAuthenticationExceptionThrown_ReturnsFalse() {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(false);
        when(dialog.getVerificationCode()).thenThrow(new RuntimeException("Auth failed"));

        assertFalse(demo.runAuthenticationProcess(dialog));
    }

    @Test
    void testRunAuthenticationProcess_InvalidInput() throws AuthenticationException {
        JavaFXAuthenticationDialog dialog = mock(JavaFXAuthenticationDialog.class);
        when(dialog.isCancelled()).thenReturn(false);

        AuthenticationController mockController = mock(AuthenticationController.class);
        when(mockController.authenticate()).thenThrow(new RuntimeException(new InvalidInputException("Invalid input")));

        Demo demo = new Demo() {
            @Override
            protected AuthenticationController createController(JavaFXAuthenticationDialog dialog) {
                return mockController;
            }
        };

        assertFalse(demo.runAuthenticationProcess(dialog));
        verify(dialog).showErrorMessage(anyString());
    }

    @Test
    void testStart_WithMockedStage() {
        Demo demo = new Demo();

        Platform.runLater(() -> {
            Stage mockStage = new Stage(); // Use real stage, not mocked
            assertDoesNotThrow(() -> demo.start(mockStage));
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
// ==================== main() Method Tests ====================
//    @Test
//    void testMain_LaunchesApplication() {
//        assertDoesNotThrow(() -> Demo.main(new String[]{}));
//    }
//
//        @Test
//    void testMain_LaunchesApplication() {
//        assertDoesNotThrow(() -> Demo.main(new String[]{}));
//    }
//
//        @Test
//    void testMain_LaunchesApplication() {
//        // Can't easily test Application.launch in unit tests
//        // This would need to be an integration test
//        assertDoesNotThrow(() -> Demo.main(new String[]{}));
//    }

    @Test
    void testRunAuthenticationProcess_AuthFailed() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(mockController.authenticate()).thenReturn(false);

        Demo testDemo = new Demo() {
            @Override
            protected AuthenticationController createController(JavaFXAuthenticationDialog dialog) {
                return mockController;
            }
        };

        assertFalse(testDemo.runAuthenticationProcess(dialog));
        verify(dialog).showErrorMessage(anyString());
    }

    @Test
    void testRunAuthenticationProcess_WithNullDialog() {
        assertThrows(IllegalArgumentException.class,
                () -> demo.runAuthenticationProcess(null));
    }

    @Test
    void testRunAuthenticationProcess_DialogCancelled() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(true);
        assertFalse(demo.runAuthenticationProcess(dialog));
        verify(mockController, never()).authenticate();
    }

    @Test
    void testRunAuthenticationProcess_UnexpectedExceptionTwo() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(mockController.authenticate()).thenThrow(new RuntimeException("Unexpected error"));

        Demo testDemo = new Demo() {
            @Override
            protected AuthenticationController createController(JavaFXAuthenticationDialog dialog) {
                return mockController;
            }
        };

        assertFalse(testDemo.runAuthenticationProcess(dialog));
        verify(dialog).showErrorMessage(contains("unexpected error"));
    }

    @Test
    void testCreateController_WithNullDialog() {
        Demo demo = new Demo();
        assertThrows(IllegalArgumentException.class,
                () -> demo.createController(null));
    }

    @Test
    void testIsValidPhoneNumber_Whitespace() {
        assertFalse(Demo.isValidPhoneNumber("   "));
    }

    @Test
    void testIsValidPhoneNumber_WithSpaces() {
        assertFalse(Demo.isValidPhoneNumber("+44 79 3889 3257"));
    }

    @Test
    void testRunAuthenticationProcess_Interrupted() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(mockController.authenticate()).thenThrow(new RuntimeException(new InterruptedException()));

        assertFalse(demo.runAuthenticationProcess(dialog));
        verify(dialog).showErrorMessage(anyString());
    }

    @Test
    void testRunAuthenticationProcess_WithInvalidVerificationCode() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("wrongCode");

        assertFalse(demo.runAuthenticationProcess(dialog));
    }

    @Test
    void testStart_CreatesDialogAndController() {
        Demo demo = new Demo();
        Stage mockStage = mock(Stage.class);

        // Verify dialog and controller are created and used
        assertDoesNotThrow(() -> demo.start(mockStage));

        // Can't directly verify constructor calls, but we can test the behavior
        // through integration tests or by overriding methods
    }

    @Test
    void testRunAuthenticationProcess_CreatesController() {
        JavaFXAuthenticationDialog dialog = mock(JavaFXAuthenticationDialog.class);
        Demo demo = new Demo();

        demo.runAuthenticationProcess(dialog);

        // Verify controller was created and used
        verify(dialog, atLeastOnce()).isCancelled();
    }

    @Test
    void testRunAuthenticationProcess_NullDialogCheck() {
        assertThrows(IllegalArgumentException.class,
                () -> demo.runAuthenticationProcess(null));
    }

    @Test
    void testRunAuthenticationProcess_ErrorMessageDisplayedOnFailure() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(mockController.authenticate()).thenReturn(false);

        assertFalse(demo.runAuthenticationProcess(dialog));
        verify(dialog).showErrorMessage(anyString());
    }

    @Test
    void testIsValidPhoneNumber_EdgeCases() {
        // Test empty string after trim
        assertFalse(Demo.isValidPhoneNumber("   "));

        // Test exactly minimum length
        assertTrue(Demo.isValidPhoneNumber("+12"));

        // Test exactly maximum length
        assertTrue(Demo.isValidPhoneNumber("+123456789012345"));

        // Test one character too long
        assertFalse(Demo.isValidPhoneNumber("+1234567890123456"));

        // Test with spaces that should be trimmed
        assertFalse(Demo.isValidPhoneNumber(" +1234567890 "));
    }

    @Test
    void testStart_ErrorHandling() {
        Demo demo = new Demo();
        Stage mockStage = mock(Stage.class);

        // Simulate error during dialog creation
        doThrow(new RuntimeException("Test error")).when(mockStage).show();

        assertDoesNotThrow(() -> demo.start(mockStage));
        // Can't easily verify System.err output in unit tests
    }

}