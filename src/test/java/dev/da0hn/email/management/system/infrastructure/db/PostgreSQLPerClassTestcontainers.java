package dev.da0hn.email.management.system.infrastructure.db;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = PostgreSQLPerClassInitializer.class)
@ActiveProfiles("test")
@SpringBootTest
public @interface PostgreSQLPerClassTestcontainers {
}
