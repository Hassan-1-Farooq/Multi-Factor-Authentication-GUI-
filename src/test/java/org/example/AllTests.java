package org.example;

import org.example.config.EmailConfigTest;
import org.example.context.AuthenticationContextTest;
import org.example.controller.AuthenticationControllerTest;
import org.example.dialog.JavaFXAuthenticationDialogTest;
import org.example.exception.AuthenticationExceptionTest;
import org.example.exception.InvalidInputExceptionTest;
import org.example.factory.EmailAuthenticationStrategyFactoryTest;
import org.example.factory.SmsAuthenticationStrategyFactoryTest;
import org.example.service.EmailServiceTest;
import org.example.service.SmsServiceTest;
import org.example.strategy.email.EmailAuthenticationStrategyTest;
import org.example.strategy.sms.SmsAuthenticationStrategyTest;
import org.example.util.LoggerUtilTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        EmailConfigTest.class,
        AuthenticationContextTest.class,
        AuthenticationControllerTest.class,
        EmailAuthenticationStrategyFactoryTest.class,
        SmsAuthenticationStrategyFactoryTest.class,
        EmailServiceTest.class,
        SmsServiceTest.class,
        EmailAuthenticationStrategyTest.class,
        SmsAuthenticationStrategyTest.class,
        DemoTest.class,
        InterClassTest.class,
        FunctionalTest.class,
        JavaFXAuthenticationDialogTest.class,
        AuthenticationExceptionTest.class,
        InvalidInputExceptionTest.class,
        LoggerUtilTest.class

})
public class AllTests {
}