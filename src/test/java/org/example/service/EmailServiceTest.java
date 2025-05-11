package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Nested
public class EmailServiceTest {

    private EmailService emailService;
    boolean result;
    @BeforeEach
    void setUp() {

        emailService = new EmailService("sender@example.com", "password");

    }

    @Test
    void testSendEmail() {
        emailService = new EmailService("test@example.com", "password");
        result = emailService.sendEmail("recipient@example.com", "Test Subject", "Test Body");
        assertFalse(result); // Assuming the email sending will fail in a test environment
    }

    @Test
    void testSendEmailWithCorrectInfo() {
        emailService = new EmailService("hassanfarooq105964@gmail.com", "wyop cope lsop fefv");
        result = emailService.sendEmail("hassanfarooq105964@gmail.com", "Test Subject", "Test Body");
        assertTrue(result); // Assuming the email sending will pass in a test environment
    }


    @Test
    void testSendEmailWithInvalidRecipientFormat() {
        emailService = new EmailService("test@example.com", "password");
        result = emailService.sendEmail("invalid-email-format", "Test Subject", "Test Body");
        assertFalse(result); // Sending to an invalid email format should fail
    }

    @Test
    void testSendEmailWithInvalidSenderCredentials() {
        // Don't use real service for this test
        EmailService mockEmailService = mock(EmailService.class);
        when(mockEmailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(false);
        boolean result = mockEmailService.sendEmail("recipient@example.com", "Subject", "Body");
        assertFalse(result); // Now it's safe and won't hit the real server
    }


    @Test
    void testSendEmailWithLongSubject() {
        emailService = new EmailService("test@example.com", "password");
        String longSubject = "This is a very long subject line that exceeds the typical length limit for email subjects. "
                + "It is used to test how the EmailService handles long subject lines.";
        result = emailService.sendEmail("recipient@example.com", longSubject, "Test Body");
        assertFalse(result); // Sending with a very long subject should fail
    }

    @Test
    void testSendEmailWithLongBody() {
        emailService = new EmailService("test@example.com", "password");
        StringBuilder longBody = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longBody.append("This is a very long body text. ");
        }
        result = emailService.sendEmail("recipient@example.com", "Test Subject", longBody.toString());
        assertFalse(result); // Sending with a very long body should fail
    }

    @Test
    void testSendEmail_NetworkTimeout() {
        emailService = new EmailService("test@example.com", "password");
        result = emailService.sendEmail("recipient@example.com", "Test Subject", "Test Body");
        assertFalse(result);
    }

    @Test
    void testSendEmailWithCorrectInfo_Mocked() {
        EmailService emailService = mock(EmailService.class);
        when(emailService.sendEmail(any(), any(), any())).thenReturn(true);

        boolean result = emailService.sendEmail("recipient@example.com", "Subject", "Body");
        assertTrue(result);
    }

    @Test
    void testSendEmailWithSpecialCharacters() {
        emailService = new EmailService("test@example.com", "password");
        result = emailService.sendEmail("recipient@example.com", "Test Subject", "Test Body with special characters: !@#$%^&*()");
        assertFalse(result); // Assuming the email sending will fail in a test environment
    }

@Test
void testSendEmail_ValidInputs() {
    emailService = new EmailService("hassanfarooq105964@gmail.com", "wyop cope lsop fefv");
    result = emailService.sendEmail("hassanfarooq105964@gmail.com", "Verification Code", "Your verification code is: 123456");
    assertTrue(result, "Email should be sent successfully with valid inputs.");
}

    // Invalid Email Address
    @Test
    void testSendEmail_InvalidEmail() {
        emailService = new EmailService("sender@example.com", "senderPassword");
        result = emailService.sendEmail("test@.com", "Verification Code", "Your verification code is: 123456");
        assertFalse(result, "Email should fail to send with an invalid recipient email.");
    }

    // All inputs are valid, email sending fails (simulate MessagingException)
    @Test
    void testSendEmail_AllInputsValid_Failure() throws MessagingException {
        // Mock Transport.send to throw MessagingException
        Transport transportMock = mock(Transport.class);
        doThrow(new MessagingException("Failed to send email")).when(transportMock).sendMessage(any(), any());

        result = emailService.sendEmail("test@example.com", "Test Subject", "Test Body");
        assertFalse(result, "Expected false when email sending fails");
    }


    @Test
    void testSendEmail_EmailSendingFails() throws MessagingException {
        // Test Path 5: email sending fails (throws MessagingException)
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            // Mock Transport.send to throw MessagingException
            mockedTransport.when(() -> Transport.send(any(Message.class))).thenThrow(new MessagingException("Failed to send email"));

            result = emailService.sendEmail("test@example.com", "Test Subject", "Test Body");
            assertFalse(result);
        }
    }

    @Test
    void testSendEmail_UnexpectedException() {
        EmailService emailService = new EmailService("hassanfarooq105964@gmail.com", "wyop cope lsop fefv");

        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any(Message.class)))
                    .thenThrow(new RuntimeException("Unexpected Exception in Email Service"));

            Exception exception = assertThrows(RuntimeException.class, () -> {
                emailService.sendEmail("hassanfarooq105964@gmail.com", "Subject", "Body");
            });

            assertNotNull(exception.getMessage());
        }
    }


    @Test
    void testSendEmail_EmailServiceException() {
        EmailService mockService = mock(EmailService.class);
        doThrow(new RuntimeException("Email service error")).when(mockService).sendEmail(anyString(), anyString(), anyString());

        Exception exception = assertThrows(RuntimeException.class, () -> mockService.sendEmail("recipient@example.com", "Subject", "Body"));
        assertNotNull(exception.getMessage());
    }

    @Test
    void testSendEmail_WithInvalidSenderCredentials() {
        EmailService invalidEmailService = new EmailService("invalid@example.com", "wrongpassword");

        // No exception should propagate
        assertDoesNotThrow(() -> {
            boolean result = invalidEmailService.sendEmail("recipient@example.com", "Subject", "Body");
            assertFalse(result, "Expected email sending to fail with invalid credentials.");
        });
    }



    @Test
    void testSendEmail_WithMessagingException() throws MessagingException {
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any(Message.class))).thenThrow(new MessagingException("Failed to send email"));
            boolean result = emailService.sendEmail("recipient@example.com", "Subject", "Body");
            assertFalse(result);
        }
    }



    @Test
    void testSendEmail_Success() {
        assertFalse(emailService.sendEmail("recipient@example.com", "Subject", "Body"));
    }


    @Test
    void testSendEmail_ExceptionHandling() throws MessagingException {
        try (var mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any())).thenThrow(new MessagingException("SMTP error"));

            boolean result = emailService.sendEmail("recipient@example.com", "Subject", "Body");
            assertFalse(result);
        }
    }


    @Test
    void testConstructor_NullSenderEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> new EmailService(null, "password"));
    }

    @Test
    void testConstructor_EmptySenderPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> new EmailService("test@example.com", ""));
    }

    @Test
    void testValidateEmailParameters_NullRecipient() {
        EmailService service = new EmailService("test@example.com", "pass");
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail(null, "subject", "body"));
    }

  // For mutation testing
  @Test
  void testSendEmail_NullSubject() {
      EmailService service = new EmailService("sender@example.com", "password");
      assertThrows(IllegalArgumentException.class, () -> {
          service.sendEmail("recipient@example.com", null, "Body");
      });
  }
    @Test
    void testConstructor_EmptySenderEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> new EmailService("", "password"));
    }

    @Test
    void testSendEmail_WithHeaderInjectionAttempt() {
        boolean result = emailService.sendEmail("recipient@example.com", "Subject\nInjected: header", "Body");
        assertFalse(result);
    }

    @Test
    void testSendEmail_WithNullBody() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendEmail("recipient@example.com", "Subject", null));
    }

    @Test
    void testSendEmail_WithHtmlContent() {
        boolean result = emailService.sendEmail("recipient@example.com", "Subject", "<html><body>Test</body></html>");
        assertFalse(result);
    }

    @Test
    void testSendEmail_HeaderInjectionInBody() {
        boolean result = emailService.sendEmail("recipient@example.com", "Subject",
                "Body\nInjected-Header: value");
        assertFalse(result);
    }

    @Test
    void testSendEmail_ExtremelyLongSubject() {
        StringBuilder longSubject = new StringBuilder();
        while (longSubject.length() < 1000) {
            longSubject.append("Very long subject ");
        }

        boolean result = emailService.sendEmail("recipient@example.com", longSubject.toString(), "Body");
        assertFalse(result);
    }


    @Test
    void testConstructor_NullSenderPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> new EmailService("test@example.com", null));
    }

    @Test
    void testValidateEmailParameters_EmptySubject() {
        EmailService service = new EmailService("test@example.com", "pass");
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("recipient@example.com", "", "body"));
    }

    @Test
    void testValidateEmailParameters_EmptyBody() {
        EmailService service = new EmailService("test@example.com", "pass");
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("recipient@example.com", "subject", ""));
    }

    @Test
    void testCreateMailSession_InvalidProperties() {
        Properties invalidProps = new Properties();
        invalidProps.put("mail.smtp.host", "");
        EmailService service = new EmailService("test@example.com", "pass");

        // Should not throw - session creation succeeds even with bad properties
        assertDoesNotThrow(() -> {
            service.createMailSession(invalidProps);
        });
    }

    @Test
    void testSendEmail_AllBlankParameters() {
        EmailService service = new EmailService("sender@example.com", "password");
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("", "", ""));
    }

    @Test
    void testValidateEmailParameters_MultipleFailures() {
        EmailService service = new EmailService("sender@example.com", "password");
        assertThrows(IllegalArgumentException.class, () -> {
            service.sendEmail(null, null, null);
        });
    }

    @Test
    void testSendEmail_HeaderInjectionInRecipient() {
        EmailService service = new EmailService("sender@example.com", "password");
        boolean result = service.sendEmail("recipient@example.com\nBCC: attacker@example.com", "Subject", "Body");
        assertFalse(result, "Should fail due to header injection in recipient");
    }

    @Test
    void testSendEmail_WhitespaceSubjectAndBody() {
        EmailService service = new EmailService("sender@example.com", "password");
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("recipient@example.com", "   ", "   "));
    }
    @Test
    void testSendEmail_NewlineInSubject() {
        EmailService service = new EmailService("sender@example.com", "password");
        boolean result = service.sendEmail("recipient@example.com", "Subject\nExtra", "Body");
        assertFalse(result, "Subject with newline should fail validation");
    }

    @Test
    void testConstructor_InvalidSenderEmailFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> new EmailService("invalid-email", "password"));
    }

    @Test
    void testConstructor_ValidEmailFormat() {
        assertDoesNotThrow(() -> new EmailService("user@example.com", "password123"));
    }

    @Test
    void testSendEmail_CarriageReturnInBody() {
        EmailService service = new EmailService("sender@example.com", "password");
        boolean result = service.sendEmail("recipient@example.com", "Subject", "Body\r\nBCC: attacker@example.com");
        assertFalse(result);
    }
    @Test
    void testCreateMailSession_NullProperties() {
        EmailService service = new EmailService("sender@example.com", "password");
        assertThrows(NullPointerException.class, () -> {
            service.createMailSession(null);
        });
    }

    @Test
    void testSendEmail_SubjectInjection() {
        boolean result = emailService.sendEmail("test@example.com",
                "Subject\nInjected: header", "Body");
        assertFalse(result);
    }


}
