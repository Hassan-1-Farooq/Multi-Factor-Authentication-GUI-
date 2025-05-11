package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionalTest {

    // Valid Cases
    @Test
    void testValidPhoneNumbers() {
        assertTrue(Demo.isValidPhoneNumber("+1234567890"));
        assertTrue(Demo.isValidPhoneNumber("+447361584298"));
        assertTrue(Demo.isValidPhoneNumber("+19876543210"));
    }

    // Too Short
    @Test
    void testInvalidShortPhoneNumbers() {
        assertFalse(Demo.isValidPhoneNumber("+1"));
        assertFalse(Demo.isValidPhoneNumber("1"));
    }

    // Too Long
    @Test
    void testInvalidLongPhoneNumbers() {
        assertFalse(Demo.isValidPhoneNumber("+1234567890123456"));
        assertFalse(Demo.isValidPhoneNumber("+11111111111111111"));
    }

    // Missing plus
    @Test
    void testMissingPlusSign() {
        assertFalse(Demo.isValidPhoneNumber("1234567890"));
    }

    // Letters & Symbols
    @Test
    void testContainsLettersOrSpecialCharacters() {
        assertFalse(Demo.isValidPhoneNumber("+123ABC789"));
        assertFalse(Demo.isValidPhoneNumber("+12-345678"));
    }

    // Leading zero after +
    @Test
    void testLeadingZeros() {
        assertFalse(Demo.isValidPhoneNumber("+0123456789"));
    }

    // Empty and null
    @Test
    void testEmptyAndNullInputs() {
        assertFalse(Demo.isValidPhoneNumber(""));
        assertFalse(Demo.isValidPhoneNumber(null));
    }

    // Edge cases
    @Test
    void testMinValidPhoneNumber() {
        assertTrue(Demo.isValidPhoneNumber("+12"));
    }

    @Test
    void testMaxValidPhoneNumber() {
        assertTrue(Demo.isValidPhoneNumber("+123456789012345"));
    }

    @Test
    void testBelowMinLength() {
        assertFalse(Demo.isValidPhoneNumber("+1"));
    }

    @Test
    void testAboveMaxLength() {
        assertFalse(Demo.isValidPhoneNumber("+1234567890123456"));
    }

    @Test
    void testContainsLetters() {
        assertFalse(Demo.isValidPhoneNumber("+123A56789"));
    }

    @Test
    void testStartsWithZero() {
        assertFalse(Demo.isValidPhoneNumber("+0123456789"));
    }

    @Test
    void testEmptyInput() {
        assertFalse(Demo.isValidPhoneNumber(""));
    }

    @Test
    void testNullInput() {
        assertFalse(Demo.isValidPhoneNumber(null));
    }
}
