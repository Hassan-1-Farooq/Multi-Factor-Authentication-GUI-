package org.example.strategy.sms;

import org.example.exception.AuthenticationException;
import org.example.service.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SmsAuthenticationStrategyTest {

    private SmsAuthenticationStrategy strategy;
    private SmsService smsService;

    @BeforeEach
    void setUp() {
        smsService = mock(SmsService.class);
        strategy = new SmsAuthenticationStrategy(smsService);
    }

    @Test
    void testSendVerificationCode_Successful() throws AuthenticationException {
        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);

        strategy.sendVerificationCode("+1234567890");

        verify(smsService, times(1)).sendSms(anyString(), anyString(), anyString());
    }

    @Test
    void testSendVerificationCode_MessageFormat() throws AuthenticationException {
        when(smsService.sendSms(anyString(), contains("Your verification code is:"), anyString())).thenReturn(true);

        strategy.sendVerificationCode("+1234567890");
        verify(smsService, times(1)).sendSms(eq("+1234567890"), contains("Your verification code is:"), anyString());
    }


    @Test
    void testSendVerificationCode_Failure() throws AuthenticationException {
        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(false);

        strategy.sendVerificationCode("+1234567890");

        verify(smsService, times(1)).sendSms(anyString(), anyString(), anyString());
    }

    @Test
    void testAuthenticate_WithValidCode() throws AuthenticationException {
        // Step 1: Mock the SMS service to send the code successfully
        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);

        // Step 2: Send the verification code (this will store the code inside the strategy)
        strategy.sendVerificationCode("+1234567890");

        // Step 3: Retrieve the stored verification code (instead of generating a new one)
        String storedCode = strategy.verificationCode;

        // Step 4: Use the stored code for authentication
        assertTrue(strategy.authenticate(storedCode, "password"));
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
    void testSendSms_MultipleAttempts() throws AuthenticationException {
        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);

        strategy.sendVerificationCode("+1234567890");
        strategy.sendVerificationCode("+1234567890");

        verify(smsService, times(2)).sendSms(anyString(), anyString(), anyString());
    }

    @Test
    void testSendSms_BlacklistedNumber() throws AuthenticationException {
        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(false);

        strategy.sendVerificationCode("+1111111111"); // Assume this is a blacklisted number
        verify(smsService, times(1)).sendSms(anyString(), anyString(), anyString());
    }

//    For Mutation Testing

    @Test
    void testSendVerificationCode_FormatsPhoneNumber() throws AuthenticationException {
        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);

        // Test with the exact format the strategy currently uses
        strategy.sendVerificationCode("+44 79 3889 3257");
        verify(smsService).sendSms(eq("+44 79 3889 3257"), anyString(), anyString());
    }
@Test
void testGenerateVerificationCode_NotNull() {
    String code = strategy.generateVerificationCode();
    assertNotNull(code);
    assertEquals(6, code.length());
}

    @Test
    void testGenerateVerificationCode_NumericOnly() {
        String code = strategy.generateVerificationCode();
        assertTrue(code.matches("\\d+"));
    }

    @Test
    void testGenerateVerificationCode_Range() {
        // Test multiple generations to verify range (100000-999999)
        for (int i = 0; i < 1000; i++) {
            String code = strategy.generateVerificationCode();
            int numericCode = Integer.parseInt(code);
            assertTrue(numericCode >= 100000 && numericCode <= 999999,
                    "Code should be between 100000 and 999999");
        }
    }

    @Test
    void testGenerateVerificationCode_Randomness() {
        // Verify different codes are generated
        String code1 = strategy.generateVerificationCode();
        String code2 = strategy.generateVerificationCode();
        assertNotEquals(code1, code2, "Subsequent codes should be different");

        // Verify all digits can vary (not just last digit)
        boolean differentDigitsFound = false;
        for (int i = 0; i < 100; i++) {
            String c1 = strategy.generateVerificationCode();
            String c2 = strategy.generateVerificationCode();
            if (!c1.substring(0,5).equals(c2.substring(0,5))) {
                differentDigitsFound = true;
                break;
            }
        }
        assertTrue(differentDigitsFound, "All digit positions should vary");
    }

    @Test
    void testConstructor_ServiceAssignment() {
        try {
            Field field = SmsAuthenticationStrategy.class.getDeclaredField("smsService");
            field.setAccessible(true);
            assertSame(smsService, field.get(strategy),
                    "smsService should be assigned to the field");
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

//    @Test
//    void testAuthenticate_CaseSensitivity() throws AuthenticationException {
//        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);
//        strategy.sendVerificationCode("+1234567890");
//        String storedCode = strategy.verificationCode;
//
//        // Test with different case
//        assertFalse(strategy.authenticate(storedCode.toUpperCase(), "password"),
//                "Authentication should be case sensitive");
//        assertFalse(strategy.authenticate(storedCode.toLowerCase(), "password"),
//                "Authentication should be case sensitive");
//    }

    @Test
    void testAuthenticate_LeadingTrailingSpaces() throws AuthenticationException {
        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);
        strategy.sendVerificationCode("+1234567890");
        String storedCode = strategy.verificationCode;

        assertFalse(strategy.authenticate(" " + storedCode + " ", "password"),
                "Authentication should be sensitive to whitespace");
    }

    @Test
    void testSendVerificationCode_CodeAssignment() throws AuthenticationException {
        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);
        strategy.sendVerificationCode("+1234567890");

        assertNotNull(strategy.verificationCode, "Verification code should be assigned");
        assertEquals(6, strategy.verificationCode.length(),
                "Verification code should be 6 digits");
    }

    @Test
    void testSendVerificationCode_ServiceCalledWithCode() throws AuthenticationException {
        when(smsService.sendSms(anyString(), anyString(), anyString())).thenReturn(true);
        strategy.sendVerificationCode("+1234567890");

        verify(smsService).sendSms(anyString(),
                contains(strategy.verificationCode), anyString());
    }

}
