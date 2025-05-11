package org.example.factory;

import org.example.strategy.AuthenticationStrategy;

/**
 * Factory interface for creating authentication strategies.
 */
public interface AuthenticationStrategyFactory {
    /**
     * Creates an authentication strategy instance.
     * @return AuthenticationStrategy implementation
     */
    AuthenticationStrategy createStrategy();
}
