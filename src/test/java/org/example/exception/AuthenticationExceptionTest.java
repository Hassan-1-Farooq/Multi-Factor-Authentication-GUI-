package org.example.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationExceptionTest {

    @Test
    void testAuthenticationException_MessageOnly() {
        AuthenticationException ex = new AuthenticationException("Auth failed");
        assertEquals("Auth failed", ex.getMessage());
    }

    @Test
    void testAuthenticationException_WithCause() {
        Exception cause = new Exception("cause");
        AuthenticationException ex = new AuthenticationException("Auth failed", cause);
        assertEquals("Auth failed", ex.getMessage());
        assertEquals("cause", ex.getCause().getMessage());
    }
}
