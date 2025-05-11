package org.example;

import org.example.context.AuthenticationContext;
import org.example.exception.AuthenticationException;
import org.example.service.EmailService;
import org.example.service.SmsService;
import org.example.strategy.email.EmailAuthenticationStrategy;
import org.example.strategy.sms.SmsAuthenticationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InterClassTest {

    private EmailService emailService;
    private EmailAuthenticationStrategy strategy;
    private AuthenticationContext context;

    @BeforeEach
    public void setUp() {
        emailService = mock(EmailService.class);
        strategy = new EmailAuthenticationStrategy(emailService);
        context = new AuthenticationContext();
        context.setStrategy(strategy);
    }

    @Test
    public void testEmailAuthenticationFlow_Success() throws AuthenticationException {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        context.sendVerificationCode("test@example.com");

        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString());
        assertNotNull(strategy.generateVerificationCode());
    }



    @Test
    public void testEmailAuthenticationFlow_NoCodeGenerated() {
        assertFalse(context.verify("some_code"));
    }

    @Test
    public void testEmailAuthenticationFlow_SpecialCharactersInEmail() throws AuthenticationException {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        context.sendVerificationCode("test+special@example.com");

        verify(emailService, times(1)).sendEmail(eq("test+special@example.com"), anyString(), anyString());
        assertNotNull(strategy.generateVerificationCode());
    }

    @Test
    public void testEmailAuthenticationFlow_VerifyWithEmptyCode() throws AuthenticationException {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        context.sendVerificationCode("test@example.com");

        assertFalse(context.verify(""));
    }

    @Test
    public void testEmailAuthenticationFlow_ConcurrentVerification() throws InterruptedException {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        Runnable task = () -> {
            try {
                context.sendVerificationCode("test@example.com");
            } catch (AuthenticationException e) {
                throw new RuntimeException(e);
            }
            assertFalse(context.verify("random_code"));
        };

        for (int i = 0; i < 10; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));
    }

    @Test
    public void testEmailAuthenticationFlow_CaseSensitivity() throws AuthenticationException {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        context.sendVerificationCode("test@example.com");

        String code = strategy.generateVerificationCode().toUpperCase();
        assertFalse(context.verify(code));
    }

    @Test
    public void testEmailAuthenticationFlow_ResendingCode() throws AuthenticationException {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        context.sendVerificationCode("test@example.com");
        context.sendVerificationCode("test@example.com");

        verify(emailService, times(2)).sendEmail(eq("test@example.com"), anyString(), anyString());
        assertNotNull(strategy.generateVerificationCode());
    }

    @Test
    public void testEmailAuthenticationFlow_TooManyIncorrectAttempts() throws AuthenticationException {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        context.sendVerificationCode("test@example.com");

        for (int i = 0; i < 3; i++) {
            assertFalse(context.verify("wrong_code"));
        }

        assertFalse(context.verify("correct_code"));
    }



    @Test
    public void testEmailAuthenticationFlow_VerifyBeforeSendingCode() {
        assertFalse(context.verify("any_code"));
    }

    @Test
    void testEmailAuthenticationFlow_WithSmsStrategy() throws AuthenticationException {
        SmsService smsService = mock(SmsService.class);
        SmsAuthenticationStrategy smsStrategy = new SmsAuthenticationStrategy(smsService);
        AuthenticationContext context = new AuthenticationContext();
        context.setStrategy(smsStrategy);

        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);

        context.sendVerificationCode("+1234567890");

        verify(smsService, times(1)).sendSms(eq("+1234567890"), anyString(), anyString());
        assertNotNull(smsStrategy.generateVerificationCode());
    }

    @Test
    void testEmailAuthenticationFlow_WithNullStrategy() {
        AuthenticationContext context = new AuthenticationContext();

        Exception exception = assertThrows(NullPointerException.class, () -> {
            context.sendVerificationCode("test@example.com");
        });

        assertNotNull(exception.getMessage());
    }

    @Test
    void testSwitchAuthenticationStrategy() throws AuthenticationException {
        SmsService smsService = mock(SmsService.class);
        SmsAuthenticationStrategy smsStrategy = new SmsAuthenticationStrategy(smsService);
        context.setStrategy(smsStrategy);

        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);
        context.sendVerificationCode("+1234567890");

        verify(smsService, times(1)).sendSms(anyString(), anyString(), anyString());
    }



}
