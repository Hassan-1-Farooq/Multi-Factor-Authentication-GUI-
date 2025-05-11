package org.example.controller;

import org.example.config.EmailConfig;
import org.example.context.AuthenticationContext;
import org.example.dialog.AuthenticationDialog;
import org.example.exception.AuthenticationException;
import org.example.exception.InvalidInputException;
import org.example.factory.AuthenticationStrategyFactory;
import org.example.factory.EmailAuthenticationStrategyFactory;
import org.example.factory.SmsAuthenticationStrategyFactory;
import org.example.strategy.AuthenticationStrategy;
import org.example.util.LoggerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    private AuthenticationDialog dialog;
    private AuthenticationController controller;

    @BeforeEach
    void setup() {
        dialog = mock(AuthenticationDialog.class);
        controller = new AuthenticationController(dialog);
    }

    @Test
    void testAuthenticate_CancelledDialog() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(true);
        assertFalse(controller.authenticate());
    }

    @Test
    void testConstructor_NullDialog_ShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new AuthenticationController(null));
    }

    @Test
    void testValidateCredentials_InvalidPassword() {
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("wrongpassword");

        assertThrows(InvalidInputException.class, () -> controller.validateCredentials());
    }

    @Test
    void testValidateCredentials_InvalidEmail() {
        AuthenticationDialog dialog = mock(AuthenticationDialog.class);
        when(dialog.getEmail()).thenReturn("wrong@example.com");
        when(dialog.getPassword()).thenReturn("correctPassword");

        try (MockedStatic<EmailConfig> mocked = Mockito.mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("valid@example.com");

            AuthenticationController controller = new AuthenticationController(dialog);
            assertThrows(InvalidInputException.class, controller::validateCredentials);
        }
    }


    @Test
    void testValidateCredentials_InvalidPhoneNumberWhenUsingSms() {
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(true);
        when(dialog.getPhoneNumber()).thenReturn("invalid");

        assertThrows(InvalidInputException.class, () -> controller.validateCredentials());
    }


    @ParameterizedTest
    @ValueSource(strings = {"+447938893257", "+1234567890", "+123456789012345"})
    void testIsValidPhoneNumber_ValidNumbers(String phoneNumber) {
        assertTrue(controller.isValidPhoneNumber(phoneNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid", "+1", "+1234567890123456", "447938893257"})
    void testIsValidPhoneNumber_InvalidNumbers(String phoneNumber) {
        assertFalse(controller.isValidPhoneNumber(phoneNumber));
    }

    @Test
    void testIsValidPhoneNumber_NullInput() {
        assertFalse(controller.isValidPhoneNumber(null));
    }

    @Test
    void testCreateStrategyFactory_EmailStrategy() {
        AuthenticationStrategyFactory factory = controller.createStrategyFactory(false);
        assertTrue(factory instanceof EmailAuthenticationStrategyFactory);
    }

    @Test
    void testCreateStrategyFactory_SmsStrategy() {
        AuthenticationStrategyFactory factory = controller.createStrategyFactory(true);
        assertTrue(factory instanceof SmsAuthenticationStrategyFactory);
    }



    @Test
    void testPerformAuthentication_SmsStrategy() throws AuthenticationException {
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(true);
        when(dialog.getPhoneNumber()).thenReturn("+447938893257");
        when(dialog.getVerificationCode()).thenReturn("123456");

        try (MockedStatic<EmailConfig> mockedConfig = Mockito.mockStatic(EmailConfig.class)) {
            mockedConfig.when(EmailConfig::getCorrectEmail).thenReturn("hassanfarooq105964@gmail.com");
            mockedConfig.when(EmailConfig::getCorrectPassword).thenReturn("HassanHa10Fa20");

            assertFalse(controller.performAuthentication());
        }
    }


    @Test
    void testVerifyCode_SuccessfulVerification() {
        AuthenticationContext context = mock(AuthenticationContext.class);
        when(context.verify("123456")).thenReturn(true);
        when(dialog.getVerificationCode()).thenReturn("123456");

        assertTrue(controller.verifyCode(context));
    }

    @Test
    void testVerifyCode_FailedVerification() {
        AuthenticationContext context = mock(AuthenticationContext.class);
        when(context.verify("wrong")).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("wrong");

        assertFalse(controller.verifyCode(context));
    }

    @Test
    void testVerifyCode_MultipleAttempts() {
        AuthenticationContext context = mock(AuthenticationContext.class);
        when(context.verify(anyString())).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("wrong1", "wrong2", "wrong3");

        assertFalse(controller.verifyCode(context));
        verify(context, times(3)).verify(anyString());
    }

    @Test
    void testVerifyCode_NullVerificationCode() {
        AuthenticationContext context = mock(AuthenticationContext.class);
        when(dialog.getVerificationCode()).thenReturn(null);

        assertFalse(controller.verifyCode(context));
        verify(context, never()).verify(anyString());
    }




    @Test
    void testCreateStrategyFactory_Email() {
        AuthenticationStrategyFactory factory = controller.createStrategyFactory(false);
        assertNotNull(factory);
        assertTrue(factory instanceof EmailAuthenticationStrategyFactory);
    }

    @Test
    void testCreateStrategyFactory_Sms() {
        AuthenticationStrategyFactory factory = controller.createStrategyFactory(true);
        assertNotNull(factory);
        assertTrue(factory instanceof SmsAuthenticationStrategyFactory);
    }

    @Test
    void testVerifyCode_AllAttemptsFail() {
        AuthenticationContext context = mock(AuthenticationContext.class);
        when(context.verify(anyString())).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("123456");

        boolean result = controller.verifyCode(context);
        assertFalse(result);
    }

    @Test
    void testVerifyCode_SuccessOnFirstAttempt() {
        AuthenticationContext context = mock(AuthenticationContext.class);
        when(context.verify("123456")).thenReturn(true);
        when(dialog.getVerificationCode()).thenReturn("123456");

        boolean result = controller.verifyCode(context);
        assertTrue(result);
    }

    @Test
    void testVerifyCode_SuccessOnLastAttempt() {
        AuthenticationContext context = mock(AuthenticationContext.class);
        when(context.verify(anyString()))
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);
        when(dialog.getVerificationCode()).thenReturn("111111", "222222", "333333");

        boolean result = controller.verifyCode(context);
        assertTrue(result);
    }

    @Test
    void testIsValidPhoneNumber_Valid() {
        assertTrue(controller.isValidPhoneNumber("+447938893257"));
    }

    @Test
    void testIsValidPhoneNumber_Invalid() {
        assertFalse(controller.isValidPhoneNumber("invalid"));
    }

    @Test
    void testIsValidPhoneNumber_Null() {
        assertFalse(controller.isValidPhoneNumber(null));
    }

    // for mutation testing


    @Test
    void testVerifyCode_ExactMaxAttempts() {
        AuthenticationContext context = mock(AuthenticationContext.class);
        when(context.verify(anyString())).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("wrong1", "wrong2", "wrong3");

        assertFalse(controller.verifyCode(context));
        verify(context, times(3)).verify(anyString());
    }


    @Test
    void testPerformAuthentication_EmailStrategySuccess() throws AuthenticationException {
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("123456");

        try (MockedStatic<EmailConfig> mocked = mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("hassanfarooq105964@gmail.com");
            mocked.when(EmailConfig::getCorrectPassword).thenReturn("HassanHa10Fa20");

            AuthenticationStrategy mockStrategy = mock(AuthenticationStrategy.class);
            when(mockStrategy.authenticate("123456", "HassanHa10Fa20")).thenReturn(true);
            when(mockStrategy.verify("123456")).thenReturn(true);

            AuthenticationStrategyFactory mockFactory = mock(AuthenticationStrategyFactory.class);
            when(mockFactory.createStrategy()).thenReturn(mockStrategy);


        }
    }

    @Test
    void testAuthenticate_SuccessfulFlow() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("123456");

        try (MockedStatic<EmailConfig> mocked = mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("hassanfarooq105964@gmail.com");
            mocked.when(EmailConfig::getCorrectPassword).thenReturn("HassanHa10Fa20");

            AuthenticationStrategy mockStrategy = mock(AuthenticationStrategy.class);
            when(mockStrategy.authenticate("123456", "HassanHa10Fa20")).thenReturn(true);

            AuthenticationStrategyFactory mockFactory = mock(AuthenticationStrategyFactory.class);
            when(mockFactory.createStrategy()).thenReturn(mockStrategy);

            AuthenticationController controller = new AuthenticationController(dialog) {
                @Override
                AuthenticationStrategyFactory createStrategyFactory(boolean useSms) {
                    return mockFactory;
                }
            };

        }
    }

    @Test
    void testAuthenticate_AuthenticationException() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(false);

        try (MockedStatic<EmailConfig> mocked = mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("hassanfarooq105964@gmail.com");
            mocked.when(EmailConfig::getCorrectPassword).thenReturn("HassanHa10Fa20");

            AuthenticationStrategy mockStrategy = mock(AuthenticationStrategy.class);
            doThrow(new AuthenticationException("Failed")).when(mockStrategy).sendVerificationCode(anyString());

            AuthenticationStrategyFactory mockFactory = mock(AuthenticationStrategyFactory.class);
            when(mockFactory.createStrategy()).thenReturn(mockStrategy);

            AuthenticationController controller = new AuthenticationController(dialog) {
                @Override
                AuthenticationStrategyFactory createStrategyFactory(boolean useSms) {
                    return mockFactory;
                }
            };

            assertThrows(AuthenticationException.class, () -> controller.authenticate());
        }
    }

    //    additional test
    @Test
    void testValidateCredentials_CorrectCredentials() throws InvalidInputException {
        when(dialog.getEmail()).thenReturn("hassanfarooq105964@gmail.com");
        when(dialog.getPassword()).thenReturn("HassanHa10Fa20");
        when(dialog.useSms()).thenReturn(false);

        try (MockedStatic<EmailConfig> mocked = mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("hassanfarooq105964@gmail.com");
            mocked.when(EmailConfig::getCorrectPassword).thenReturn("HassanHa10Fa20");

            assertDoesNotThrow(() -> controller.validateCredentials());
        }
    }



    @Test
    void testValidateCredentials_ValidEmailAuth() throws InvalidInputException {
        // Setup
        AuthenticationDialog dialog = mock(AuthenticationDialog.class);
        when(dialog.getEmail()).thenReturn("valid@example.com");
        when(dialog.getPassword()).thenReturn("correctPassword");
        when(dialog.useSms()).thenReturn(false);

        try (MockedStatic<EmailConfig> mocked = Mockito.mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("valid@example.com");
            mocked.when(EmailConfig::getCorrectPassword).thenReturn("correctPassword");

            AuthenticationController controller = new AuthenticationController(dialog);
            assertDoesNotThrow(controller::validateCredentials);
        }
    }

    @Test
    void testValidateCredentials_InvalidSmsPhoneNumber() {
        AuthenticationDialog dialog = mock(AuthenticationDialog.class);
        when(dialog.getEmail()).thenReturn("valid@example.com");
        when(dialog.getPassword()).thenReturn("correctPassword");
        when(dialog.useSms()).thenReturn(true);
        when(dialog.getPhoneNumber()).thenReturn("invalid");

        try (MockedStatic<EmailConfig> mocked = Mockito.mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("valid@example.com");
            mocked.when(EmailConfig::getCorrectPassword).thenReturn("correctPassword");

            AuthenticationController controller = new AuthenticationController(dialog);
            assertThrows(InvalidInputException.class, controller::validateCredentials);
        }
    }

    @Test
    void testPerformAuthentication_WithMockFactory() throws AuthenticationException {
        AuthenticationStrategy mockStrategy = mock(AuthenticationStrategy.class);
        when(mockStrategy.authenticate(anyString(), anyString())).thenReturn(true);

        AuthenticationStrategyFactory mockFactory = mock(AuthenticationStrategyFactory.class);
        when(mockFactory.createStrategy()).thenReturn(mockStrategy);

        AuthenticationController controller = new AuthenticationController(dialog) {
            @Override
            AuthenticationStrategyFactory createStrategyFactory(boolean useSms) {
                return mockFactory;
            }
        };

        when(dialog.getVerificationCode()).thenReturn("123456");
        assertTrue(controller.performAuthentication());
    }
    @Test
    void testAuthenticate_LogsErrorOnInvalidInput() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("wrong@example.com");

        try (MockedStatic<EmailConfig> mockedEmail = mockStatic(EmailConfig.class);
             MockedStatic<LoggerUtil> mockedLogger = mockStatic(LoggerUtil.class)) {
            mockedEmail.when(EmailConfig::getCorrectEmail).thenReturn("correct@example.com");

            controller.authenticate();

            mockedLogger.verify(() ->
                    LoggerUtil.logError(eq("Authentication input validation failed"), any(InvalidInputException.class)));
        }
    }

    @Test
    void testAuthenticate_ShowsErrorMessageOnInvalidInput() throws AuthenticationException {
        when(dialog.isCancelled()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("wrong@example.com");

        try (MockedStatic<EmailConfig> mocked = mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("correct@example.com");

            controller.authenticate();

            verify(dialog).showErrorMessage(anyString());
        }
    }

    @Test
    void testValidateCredentials_CallsGetPhoneNumberWhenUsingSms() throws InvalidInputException {
        when(dialog.useSms()).thenReturn(true);
        when(dialog.getEmail()).thenReturn("test@example.com");
        when(dialog.getPassword()).thenReturn("password");
        when(dialog.getPhoneNumber()).thenReturn("+1234567890");

        try (MockedStatic<EmailConfig> mocked = mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("test@example.com");
            mocked.when(EmailConfig::getCorrectPassword).thenReturn("password");

            controller.validateCredentials();

            verify(dialog).getPhoneNumber();
        }
    }

    @Test
    void testValidateCredentials_EmailEqualityCheck() {
        when(dialog.getEmail()).thenReturn("test@example.com");

        try (MockedStatic<EmailConfig> mocked = mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("different@example.com");

            assertThrows(InvalidInputException.class, () -> controller.validateCredentials());
        }
    }

    @Test
    void testValidateCredentials_PasswordEqualityCheck() {
        when(dialog.getEmail()).thenReturn("test@example.com");
        when(dialog.getPassword()).thenReturn("password");

        try (MockedStatic<EmailConfig> mocked = mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("test@example.com");
            mocked.when(EmailConfig::getCorrectPassword).thenReturn("different");

            assertThrows(InvalidInputException.class, () -> controller.validateCredentials());
        }
    }

    @Test
    void testValidateCredentials_CallsIsValidPhoneNumber() throws InvalidInputException {
        when(dialog.useSms()).thenReturn(true);
        when(dialog.getEmail()).thenReturn("test@example.com");
        when(dialog.getPassword()).thenReturn("password");
        when(dialog.getPhoneNumber()).thenReturn("+1234567890");

        try (MockedStatic<EmailConfig> mocked = mockStatic(EmailConfig.class)) {
            mocked.when(EmailConfig::getCorrectEmail).thenReturn("test@example.com");
            mocked.when(EmailConfig::getCorrectPassword).thenReturn("password");

            AuthenticationController spyController = spy(controller);
            spyController.validateCredentials();

            verify(spyController).isValidPhoneNumber("+1234567890");
        }
    }

    @Test
    void testPerformAuthentication_GetsCorrectRecipient() throws AuthenticationException {
        when(dialog.useSms()).thenReturn(true);
        when(dialog.getPhoneNumber()).thenReturn("+1234567890");
        when(dialog.getEmail()).thenReturn("test@example.com");

        AuthenticationStrategyFactory mockFactory = mock(AuthenticationStrategyFactory.class);
        when(mockFactory.createStrategy()).thenReturn(mock(AuthenticationStrategy.class));

        AuthenticationController controller = new AuthenticationController(dialog) {
            @Override
            AuthenticationStrategyFactory createStrategyFactory(boolean useSms) {
                return mockFactory;
            }
        };

        controller.performAuthentication();

        verify(dialog).getPhoneNumber();
        verify(dialog, never()).getEmail();
    }
    @Test
    void testPerformAuthentication_CallsSendVerificationCode() throws AuthenticationException {
        when(dialog.useSms()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("test@example.com");

        AuthenticationStrategy mockStrategy = mock(AuthenticationStrategy.class);
        AuthenticationStrategyFactory mockFactory = mock(AuthenticationStrategyFactory.class);
        when(mockFactory.createStrategy()).thenReturn(mockStrategy);

        AuthenticationController controller = new AuthenticationController(dialog) {
            @Override
            AuthenticationStrategyFactory createStrategyFactory(boolean useSms) {
                return mockFactory;
            }
        };

        controller.performAuthentication();

        verify(mockStrategy).sendVerificationCode("test@example.com");
    }

    @Test
    void testPerformAuthentication_CallsVerifyCode() throws AuthenticationException {
        when(dialog.useSms()).thenReturn(false);
        when(dialog.getEmail()).thenReturn("test@example.com");
        when(dialog.getVerificationCode()).thenReturn("123456");

        AuthenticationStrategy mockStrategy = mock(AuthenticationStrategy.class);
        when(mockStrategy.verify("123456")).thenReturn(true);

        AuthenticationStrategyFactory mockFactory = mock(AuthenticationStrategyFactory.class);
        when(mockFactory.createStrategy()).thenReturn(mockStrategy);

        AuthenticationController spyController = spy(new AuthenticationController(dialog) {
            @Override
            AuthenticationStrategyFactory createStrategyFactory(boolean useSms) {
                return mockFactory;
            }
        });

        spyController.performAuthentication();

        verify(spyController).verifyCode(any(AuthenticationContext.class));
    }

    @Test
    void testVerifyCode_ShowsCorrectRemainingAttempts() {
        AuthenticationContext context = mock(AuthenticationContext.class);
        when(context.verify(anyString())).thenReturn(false);
        when(dialog.getVerificationCode()).thenReturn("wrong");

        controller.verifyCode(context);

        verify(dialog).showErrorMessage("Invalid verification code. Attempts remaining: 2");
        verify(dialog).showErrorMessage("Invalid verification code. Attempts remaining: 1");
        verify(dialog).showErrorMessage("Invalid verification code. Attempts remaining: 0");
    }

}