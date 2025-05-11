package org.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LoggerUtilTest {

    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        mockLogger = mock(Logger.class);
        LoggerUtil.logger = mockLogger;
    }

    @Test
    void testLogInfo() {
        Logger mockLogger = mock(Logger.class);
        LoggerUtil.logger = mockLogger;

        LoggerUtil.logInfo("test message");
        verify(mockLogger).log(Level.INFO, "test message");
    }

    @Test
    void testLogError_WithException() {
        Logger mockLogger = mock(Logger.class);
        LoggerUtil.logger = mockLogger;
        Exception ex = new Exception("test");

        LoggerUtil.logError("test error", ex);
        verify(mockLogger).log(Level.SEVERE, "test error", ex);
    }

    @Test
    void testLogError_MessageOnly() {
        Logger mockLogger = mock(Logger.class);
        LoggerUtil.logger = mockLogger;

        LoggerUtil.logError("test error");
        verify(mockLogger).log(Level.SEVERE, "test error");
    }

    @Test
    void testLogInfo_NullMessage() {
        LoggerUtil.logInfo(null);
        verify(mockLogger).log(Level.INFO, (String) null);
    }

    @Test
    void testLogInfo_EmptyMessage() {
        LoggerUtil.logInfo("");
        verify(mockLogger).log(Level.INFO, "");
    }

    @Test
    void testLogInfo_SpecialCharacters() {
        String message = "Test message with special chars: \n\t©®";
        LoggerUtil.logInfo(message);
        verify(mockLogger).log(Level.INFO, message);
    }

    // === Test logError() (message only) ===
    @Test
    void testLogError_NullMessage() {
        LoggerUtil.logError(null);
        verify(mockLogger).log(Level.SEVERE, (String) null);
    }

    @Test
    void testLogError_EmptyMessage() {
        LoggerUtil.logError("");
        verify(mockLogger).log(Level.SEVERE, "");
    }


    @Test
    void testLogError_WithRuntimeException() {
        RuntimeException ex = new RuntimeException("Test exception");
        LoggerUtil.logError("Runtime error", ex);
        verify(mockLogger).log(Level.SEVERE, "Runtime error", ex);
    }

    @Test
    void testLogError_WithCheckedException() {
        Exception ex = new Exception("Checked exception");
        LoggerUtil.logError("Checked error", ex);
        verify(mockLogger).log(Level.SEVERE, "Checked error", ex);
    }

    @Test
    void testLogError_WithChainedExceptions() {
        Exception cause = new Exception("Root cause");
        Exception ex = new Exception("Wrapper exception", cause);
        LoggerUtil.logError("Chained error", ex);
        verify(mockLogger).log(Level.SEVERE, "Chained error", ex);
    }

    // === Test Thread Safety ===
    @Test
    void testLogging_ThreadSafety() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                LoggerUtil.logInfo("Thread " + threadId + " message");
                latch.countDown();
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        verify(mockLogger, times(numThreads)).log(any(Level.class), anyString());
    }

    // === Test Log Level Correctness ===
    @Test
    void testLogLevel_Info() {
        LoggerUtil.logInfo("Test info");
        verify(mockLogger).log(eq(Level.INFO), anyString());
    }

    @Test
    void testLogLevel_Error() {
        LoggerUtil.logError("Test error");
        verify(mockLogger).log(eq(Level.SEVERE), anyString());
    }

    // === Test Edge Cases ===
    @Test
    void testLoggerUtil_WithRealLogger() {
        Logger realLogger = Logger.getLogger(LoggerUtilTest.class.getName());
        LoggerUtil.logger = realLogger;

        // Should not throw
        assertDoesNotThrow(() -> LoggerUtil.logInfo("Test with real logger"));
        assertDoesNotThrow(() -> LoggerUtil.logError("Error with real logger", new Exception("Test")));
    }

}

