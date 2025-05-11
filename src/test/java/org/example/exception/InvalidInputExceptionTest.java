package org.example.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidInputExceptionTest {

    @Test
    void testInvalidInputException_MessageOnly() {
        InvalidInputException ex = new InvalidInputException("Invalid input");
        assertEquals("Invalid input", ex.getMessage());
    }

    @Test
    void testInvalidInputException_WithCause() {
        Exception cause = new Exception("cause");
        InvalidInputException ex = new InvalidInputException("Invalid input", cause);
        assertEquals("Invalid input", ex.getMessage());
        assertEquals("cause", ex.getCause().getMessage());
    }
}
