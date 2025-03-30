package com.example.loanmanagementservice;

import com.example.loanmanagementservice.testHelpers.CustomConfiguration;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

@Testcontainers
@SpringBootTest(classes = {CustomConfiguration.class, LoanManagementServiceApplication.class})
@ActiveProfiles({"local", "test"})
@Slf4j
@AutoConfigureMockMvc
@SuppressWarnings("java:S2187")
public abstract class AbstractIntegrationTest {

    private static final MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass")
                    .withInitScript("schema.sql");

    private static final RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:3.9-management")
                    .withExposedPorts(5672, 15672);

    @BeforeAll
    static void startContainers() {
        mysqlContainer.start();
        rabbitMQContainer.start();
    }

    @AfterAll
    static void stopContainers() {
        mysqlContainer.stop();
        rabbitMQContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL properties
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        // RabbitMQ properties
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(5672));
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.rabbitmq.virtual-host", () -> "/");
    }

    public void awaitQueueIsNotEmpty(RabbitTemplate rabbitTemplate, String queue, Integer timeout) {
        Awaitility.await()
                .atMost(timeout, TimeUnit.SECONDS)
                .until(() -> rabbitTemplate.execute(it -> {
                    AMQP.Queue.DeclareOk ok = it.queueDeclarePassive(queue);
                    return ok.getMessageCount() > 0;
                }), CoreMatchers.is(true));
    }
}
