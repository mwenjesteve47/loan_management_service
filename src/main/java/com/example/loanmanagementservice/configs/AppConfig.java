package com.example.loanmanagementservice.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@RequiredArgsConstructor
public class AppConfig {
    @Value("${rabbitmq.loan.limit.exchange}")
    private String exchange;

    @Value("${rabbitmq.loan.limit.routingKey}")
    private String routingKey;
    @Value("${rabbitmq.loan.limit.queue}")
    private String queueName;
    @Value("${rabbitmq.notification.exchange}")
    private String notificationExchange;

    @Value("${rabbitmq.notification.routingKey}")
    private String notificationRoutingKey;
    @Value("${rabbitmq.notification.queue}")
    private String notificationQueue;

    @Value("${rabbitmq.loanOverdueNotification.routingKey}")
    private String loanOverdueNotificationRoutingKey;
    @Value("${rabbitmq.loanOverdueNotification.queue}")
    private String loanOverdueNotificationQueue;
    private final ConnectionFactory connectionFactory;
    @Value("${app.loan.manager.cron-expression}")
    private String cronExpression;
}
