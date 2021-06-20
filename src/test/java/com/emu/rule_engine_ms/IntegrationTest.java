package com.emu.rule_engine_ms;

import com.emu.rule_engine_ms.RuleEngineMsApp;
import com.emu.rule_engine_ms.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { RuleEngineMsApp.class, TestSecurityConfiguration.class })
public @interface IntegrationTest {
}
