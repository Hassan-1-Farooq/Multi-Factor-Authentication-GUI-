package org.example.factory;

import org.example.service.SmsService;
import org.example.strategy.AuthenticationStrategy;
import org.example.strategy.sms.SmsAuthenticationStrategy;

/**
 * Factory class for creating SMS authentication strategies.
 */
public class SmsAuthenticationStrategyFactory implements AuthenticationStrategyFactory {
    /**
     * Creates an SmsAuthenticationStrategy with required services.
     * @return SmsAuthenticationStrategy instance
     */
    @Override
    public AuthenticationStrategy createStrategy() {
        SmsService smsService = new SmsService();
        return new SmsAuthenticationStrategy(smsService);
    }
}
