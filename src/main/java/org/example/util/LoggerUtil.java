package org.example.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for application logging.
 */
public class LoggerUtil {
    static Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    /**
     * Logs an informational message.
     * @param message The message to log
     */
    public static void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    /**
     * Logs an error message.
     * @param message The error message
     */
    public static void logError(String message) {
        logger.log(Level.SEVERE, message);
    }

    /**
     * Logs an error message with exception.
     * @param message The error message
     * @param throwable The exception
     */
    public static void logError(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
}
