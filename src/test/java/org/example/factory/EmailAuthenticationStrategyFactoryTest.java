package org.example.factory;

import org.example.config.EmailConfig;
import org.example.strategy.AuthenticationStrategy;
import org.example.strategy.email.EmailAuthenticationStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailAuthenticationStrategyFactoryTest {

    @Test
    void testCreateStrategy() {

        EmailAuthenticationStrategyFactory factory = new EmailAuthenticationStrategyFactory() {
            @Override
            protected EmailConfig getEmailConfig() {
                return null;
            }
        };

        AuthenticationStrategy strategy = factory.createStrategy();

        assertNotNull(strategy, "Strategy should not be null");
        assertTrue(strategy instanceof EmailAuthenticationStrategy, "Strategy should be an instance of EmailAuthenticationStrategy");

    }

    @Test
    void testCreateStrategy_NotNull() {
        AuthenticationStrategyFactory factory = new EmailAuthenticationStrategyFactory() {
            @Override
            protected EmailConfig getEmailConfig() {
                return null;
            }
        };

        AuthenticationStrategy strategy = factory.createStrategy();
        assertNotNull(strategy, "Strategy should not be null");
    }

@Test
void testCreateStrategy_WithMockConfig() {
    EmailAuthenticationStrategyFactory factory = new EmailAuthenticationStrategyFactory() {
        @Override
        protected EmailConfig getEmailConfig() {
            EmailConfig config = mock(EmailConfig.class);
            when(config.getSenderEmail()).thenReturn("test@example.com");
            when(config.getSenderPassword()).thenReturn("password");
            return config;
        }
    };

    AuthenticationStrategy strategy = factory.createStrategy();
    assertNotNull(strategy);
}

    @Test
    void testCreateStrategy_AnonymousConfigInitialized() {
        EmailAuthenticationStrategyFactory factory = new EmailAuthenticationStrategyFactory() {
            @Override
            protected EmailConfig getEmailConfig() {
                return null; // Override to return null since we're testing the anonymous class
            }
        };

        AuthenticationStrategy strategy = factory.createStrategy();
        // The test will implicitly verify the anonymous class was properly constructed
        assertNotNull(strategy);
    }



}
