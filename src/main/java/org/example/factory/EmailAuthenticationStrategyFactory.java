package org.example.factory;

import org.example.config.EmailConfig;
import org.example.service.EmailService;
import org.example.strategy.AuthenticationStrategy;
import org.example.strategy.email.EmailAuthenticationStrategy;

import java.io.InputStream;

/**
 * Factory class for creating email authentication strategies.
 */
public abstract class EmailAuthenticationStrategyFactory implements AuthenticationStrategyFactory {
    /**
     * Creates an EmailAuthenticationStrategy with required services.
     * @return EmailAuthenticationStrategy instance
     */
    @Override
    public AuthenticationStrategy createStrategy() {
        EmailConfig emailConfig = new EmailConfig() {
            @Override
            protected InputStream getConfigInputStream() {
                return null;
            }
        };
        EmailService emailService = new EmailService(emailConfig.getSenderEmail(), emailConfig.getSenderPassword());
        return new EmailAuthenticationStrategy(emailService);
    }

    /**
     * Abstract method to get email configuration.
     * @return EmailConfig instance
     */
    protected abstract EmailConfig getEmailConfig();
}