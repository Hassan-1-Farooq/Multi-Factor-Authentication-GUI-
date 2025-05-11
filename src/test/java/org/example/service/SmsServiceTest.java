package org.example.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SmsServiceTest {

    private SmsService smsService;
    private static final String VALID_PHONE_NUMBER = "+44938893257";
    private static final String CONTEXT = "TestContext";

    @BeforeEach
    void setUp() {
        smsService = new SmsService();
    }

    @Test
    void testSendSms_Successful() {
        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreatorMock = mock(MessageCreator.class);
            mockedMessage.when(() ->
                    Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), any(String.class))
            ).thenReturn(messageCreatorMock);
            when(messageCreatorMock.create()).thenReturn(mock(Message.class));

            boolean result = smsService.sendSms(VALID_PHONE_NUMBER, "Test SMS Message", CONTEXT);
            assertTrue(result);
        }
    }

    @Test
    void testSendSms_Failure() {
        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreatorMock = mock(MessageCreator.class);
            mockedMessage.when(() ->
                    Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), any(String.class))
            ).thenReturn(messageCreatorMock);
            when(messageCreatorMock.create()).thenThrow(new RuntimeException("Twilio Error"));

            boolean result = smsService.sendSms(VALID_PHONE_NUMBER, "Test SMS Message", CONTEXT);
            assertFalse(result);
        }
    }

    @Test
    void testSendSms_NullRecipient() {
        boolean result = smsService.sendSms(null, "Test SMS Message", CONTEXT);
        assertFalse(result);
    }

    @Test
    void testSendSms_EmptyMessage() {
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, "", CONTEXT);
        assertFalse(result);
    }

    @Test
    void testSendSms_WithNullMessage() {
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, null, CONTEXT);
        assertFalse(result);
    }

    @Test
    void testSendSms_WithInvalidPhoneNumber() {
        boolean result = smsService.sendSms("invalid_number", "Test SMS Message", CONTEXT);
        assertFalse(result);
    }

    @Test
    void testSendSms_InvalidTwilioCredentials() {
        SmsService service = new SmsService();
        boolean result = service.sendSms("+1234567890", "Test SMS Message", CONTEXT);
        assertFalse(result);
    }

    // For Mutation testing:

    @Test
    void testSendSms_WhitespaceMessage() {
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, "   ", CONTEXT);
        assertFalse(result);
    }
    @Test
    void testSendSms_NullContext() {
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, "Message", null);
        assertFalse(result);
    }

    @Test
    void testSendSms_WithVeryLongMessage() {
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("This is a very long message. ");
        }

        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, longMessage.toString(), CONTEXT);
        assertFalse(result);
    }

    @Test
    void testSendSms_WithSpecialCharacters() {
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, "Message with special chars: \n\t©®", CONTEXT);
        assertFalse(result);
    }

    @Test
    void testSendSms_EmptyPhoneNumberAndMessage() {
        boolean result = smsService.sendSms("", "", "context");
        assertFalse(result);
    }

    @Test
    void testSendSms_InvalidInputs_ReturnsFalse() {
        assertFalse(smsService.sendSms("", "", ""));
        assertFalse(smsService.sendSms(null, null, null));
    }

    @Test
    void testSendSms_WithNullContext() {
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, "Test message", null);
        assertFalse(result);
    }

    @Test
    void testSendSms_WithEmptyContext() {
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, "Test message", "");
        assertFalse(result);
    }
    @Test
    void testSendSms_MessageInjection() {
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER,
                "Message\nInjected: header", CONTEXT);
        assertFalse(result);
    }

    @Test
    void testSendSms_MaxLengthMessage() {
        String longMessage = new String(new char[1600]).replace('\0', 'a');
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, longMessage, CONTEXT);
        assertFalse(result);
    }

    @Test
    void testConstructor_TwilioInitFailure() {
        try (MockedStatic<Twilio> mockedTwilio = mockStatic(Twilio.class)) {
            mockedTwilio.when(() -> Twilio.init(anyString(), anyString()))
                    .thenThrow(new RuntimeException("Init failed"));

            assertThrows(RuntimeException.class, () -> new SmsService());
        }
    }

    @Test
    void testSendSms_EmptyAfterTrim() {
        boolean result = smsService.sendSms("   ", "   ", "   ");
        assertFalse(result);
    }

    @Test
    void testConstructor_LoadsConfigProperties() {
        try (MockedStatic<Twilio> mockedTwilio = mockStatic(Twilio.class)) {
            SmsService service = new SmsService();

            assertNotNull(service.accountSid);
            assertNotNull(service.authToken);
            assertNotNull(service.twilioPhoneNumber);
            mockedTwilio.verify(() -> Twilio.init(anyString(), anyString()));
        }
    }

    @Test
    void testConstructor_InitializesTwilio() {
        try (MockedStatic<Twilio> mockedTwilio = mockStatic(Twilio.class)) {
            new SmsService();
            mockedTwilio.verify(() -> Twilio.init(anyString(), anyString()));
        }
    }

    @Test
    void testSendSms_WhitespaceOnlyPhoneNumber() {
        boolean result = smsService.sendSms("   ", "Test message", CONTEXT);
        assertFalse(result);
    }

    @Test
    void testSendSms_WhitespaceOnlyMessage() {
        boolean result = smsService.sendSms(VALID_PHONE_NUMBER, "   ", CONTEXT);
        assertFalse(result);
    }

    @Test
    void testConstructor_WhitespaceCredentials() {
        // Tests string trim() mutants
        assertThrows(IllegalArgumentException.class,
                () -> new EmailService("  ", "password"));
        assertThrows(IllegalArgumentException.class,
                () -> new EmailService("test@example.com", "   "));
    }

    @Test
    void testValidateEmailParameters_WhitespaceOnly() {
        // Tests string trim() and isEmpty() mutants
        EmailService service = new EmailService("test@example.com", "password");

        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("  ", "subject", "body"));
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("test@example.com", "  ", "body"));
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("test@example.com", "subject", "  "));
    }

    @Test
    void testConfigureSmtpProperties_AllPropertiesSet() {
        // Tests Properties.put() mutants
        EmailService service = new EmailService("test@example.com", "password");
        Properties props = service.configureSmtpProperties();

        assertNotNull(props.get("mail.smtp.host"));
        assertNotNull(props.get("mail.smtp.port"));
        assertNotNull(props.get("mail.smtp.auth"));
        assertNotNull(props.get("mail.smtp.ssl.enable"));
        assertNotNull(props.get("mail.smtp.socketFactory.class"));
    }

    @Test
    void testValidateEmailParameters_EmptyAfterTrim() {
        EmailService service = new EmailService("test@example.com", "password");

        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail(" \t\n", "subject", "body"));
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("test@example.com", " \t\n", "body"));
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("test@example.com", "subject", " \t\n"));
    }

    @Test
    void testConstructor_InvalidEmailFormat_EmptyAfterTrim() {
        assertThrows(IllegalArgumentException.class,
                () -> new EmailService(" \t\n", "password"));
    }


}
