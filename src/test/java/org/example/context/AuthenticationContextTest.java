package org.example.context;

import org.example.exception.AuthenticationException;
import org.example.strategy.AuthenticationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationContextTest {

    private AuthenticationContext context;
    private AuthenticationStrategy strategy;

    @BeforeEach
    void setUp() {
        context = new AuthenticationContext();
        strategy = mock(AuthenticationStrategy.class);
        context.setStrategy(strategy);
    }

    @Test
    void testSetStrategy() {
        try {
            Field field = AuthenticationContext.class.getDeclaredField("strategy");
            field.setAccessible(true);
            assertEquals(strategy, field.get(context));
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    @Test
    void testSendVerificationCode() throws AuthenticationException {
        context.sendVerificationCode("test@example.com");
        verify(strategy, times(1)).sendVerificationCode("test@example.com");
    }


    @Test
    void testSendVerificationCode_WithNullStrategy() {
        AuthenticationContext context = new AuthenticationContext();

        Exception exception = assertThrows(NullPointerException.class, () -> {
            context.sendVerificationCode("test@example.com");
        });

        assertNotNull(exception.getMessage());
    }

    @Test
    void testVerify_WithCorrectCode() {
        when(strategy.authenticate("123456", "password")).thenReturn(true);
        assertTrue(context.verify("123456"));
    }




    @Test
    void testVerify_WithIncorrectCode() {
        when(strategy.authenticate("wrongCode", "password")).thenReturn(false);
        assertFalse(context.verify("wrongCode"));
    }

    @Test
    void testVerify_WithNullStrategy() {
        AuthenticationContext context = new AuthenticationContext();
        Exception exception = assertThrows(NullPointerException.class, () -> context.verify("123456"));
        assertNotNull(exception.getMessage());
    }

    @Test
    void testSetStrategy_Null() {
        context.setStrategy(null);
        assertThrows(NullPointerException.class, () -> context.verify("123456"));
    }




}
