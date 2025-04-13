package dev.da0hn.email.management.system.infrastructure.db;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLPerClassInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:16.3")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");
        postgresContainer.start();
    }

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
            "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
            "spring.datasource.username=" + postgresContainer.getUsername(),
            "spring.datasource.password=" + postgresContainer.getPassword(),
            "spring.datasource.driver-class-name=" + postgresContainer.getDriverClassName(),
            "spring.flyway.url=" + postgresContainer.getJdbcUrl()
        ).applyTo(applicationContext.getEnvironment());

        applicationContext.getBeanFactory().registerSingleton("postgresContainer", postgresContainer);
    }

}
