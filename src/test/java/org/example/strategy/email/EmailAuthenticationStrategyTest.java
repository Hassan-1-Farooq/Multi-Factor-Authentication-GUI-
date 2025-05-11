
package org.example.strategy.email;

import org.example.exception.AuthenticationException;
import org.example.service.EmailService;
import org.example.service.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailAuthenticationStrategyTest {


    private EmailService emailService;
    private EmailAuthenticationStrategy strategy;
    private SmsService smsService;

    @BeforeEach
    void setUp() {
        smsService = mock(SmsService.class);
        emailService = mock(EmailService.class);
        strategy = new EmailAuthenticationStrategy(emailService);
    }



    @Test
    void testSendVerificationCode_EmailSentSuccessfully() throws AuthenticationException {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        strategy.sendVerificationCode("test@example.com");

        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString());
    }


    @Test
    void testGenerateVerificationCode_Format() {
        String code = strategy.generateVerificationCode();
        assertNotNull(code);
        assertTrue(code.matches("\\d{6}"), "Code should be 6-digit numeric");
    }


    @Test
    void testSendVerificationCode_ValidEmail() throws AuthenticationException {
        // Mock EmailService
        EmailService emailService = Mockito.mock(EmailService.class);
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        EmailAuthenticationStrategy strategy = new EmailAuthenticationStrategy(emailService);

        // Test with valid email
        strategy.sendVerificationCode("hassanfarooq105964@gmail.com");

        // Verify that the email was sent
        verify(emailService, times(1)).sendEmail(eq("hassanfarooq105964@gmail.com"), eq("Your Verification Code"), contains("Your verification code is:"));
    }


    @Test
    void testSendVerificationCode_EmailServiceThrowsException() {
        // Arrange
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("Email service unavailable"));
        EmailAuthenticationStrategy strategy = new EmailAuthenticationStrategy(emailService);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            strategy.sendVerificationCode("test@example.com");
        });
    }


    @Test
    void testAuthenticate_WithInvalidCode() {
        assertFalse(strategy.authenticate("wrong_code", "password"));
    }

    @Test
    void testVerify_ShouldReturnNull() {
        assertNull(strategy.verify("123456"));
    }


    @Test
    void testSendVerificationCode_EmailFails() {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(false);
        assertThrows(AuthenticationException.class, () -> strategy.sendVerificationCode("test@example.com"));
    }

    @Test
    void testAuthenticate_BeforeSendingCode() {
        EmailService mockService = mock(EmailService.class);
        EmailAuthenticationStrategy strategy = new EmailAuthenticationStrategy(mockService);

        assertFalse(strategy.authenticate("anycode", "password"));
    }

    @Test
    void testSendVerificationCode_ServiceFailure() {
        EmailService mockService = mock(EmailService.class);
        when(mockService.sendEmail(any(), any(), any())).thenReturn(false);

        EmailAuthenticationStrategy strategy = new EmailAuthenticationStrategy(mockService);
        assertThrows(AuthenticationException.class,
                () -> strategy.sendVerificationCode("test@example.com"));
    }


//    For mutation Testing
    @Test
    void testGenerateVerificationCode_Uniqueness() {
        EmailService mockService = mock(EmailService.class);
        EmailAuthenticationStrategy strategy = new EmailAuthenticationStrategy(mockService);

        String code1 = strategy.generateVerificationCode();
        String code2 = strategy.generateVerificationCode();
        assertNotEquals(code1, code2);
    }

    @Test
    void testGenerateVerificationCode_AlwaysSixDigits() {
        for (int i = 0; i < 100; i++) {
            String code = strategy.generateVerificationCode();
            assertEquals(6, code.length());
            assertTrue(code.matches("\\d+"));
        }
    }

    @Test
    void testAuthenticate_AfterMultipleFailedAttempts() throws AuthenticationException {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
        strategy.sendVerificationCode("test@example.com");
        String correctCode = strategy.verificationCode;

        // Multiple wrong attempts
        assertFalse(strategy.authenticate("wrong1", "password"));
        assertFalse(strategy.authenticate("wrong2", "password"));

        assertTrue(strategy.authenticate(correctCode, "password"));
    }

    @Test
    void testAuthenticate_EmptyCode() {
        assertFalse(strategy.authenticate("", "password"));
    }

    @Test
    void testAuthenticate_NullPassword() {
        assertFalse(strategy.authenticate("123456", null));
    }

    @Test
    void testGenerateVerificationCode_Range() {
        for (int i = 0; i < 1000; i++) { // Test multiple times due to randomness
            String code = strategy.generateVerificationCode();
            int numericCode = Integer.parseInt(code);
            assertTrue(numericCode >= 100000 && numericCode <= 999999,
                    "Code should be between 100000 and 999999");
        }
    }

}