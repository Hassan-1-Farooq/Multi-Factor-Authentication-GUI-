package org.example.factory;

import org.example.strategy.AuthenticationStrategy;
import org.example.strategy.sms.SmsAuthenticationStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SmsAuthenticationStrategyFactoryTest {


    @Test
    void testCreateStrategy_ShouldReturnSmsAuthenticationStrategy() {
        SmsAuthenticationStrategyFactory factory = new SmsAuthenticationStrategyFactory();
        AuthenticationStrategy strategy = factory.createStrategy();

        assertNotNull(strategy);
        assertTrue(strategy instanceof SmsAuthenticationStrategy);
    }



}
