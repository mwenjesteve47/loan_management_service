package com.example.loanmanagementservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@RequiredArgsConstructor
public class RabbitMQConfig {
    private final AppConfig appConfig;
    private final ConnectionFactory connectionFactory;


    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }


    @Bean
    DirectExchange loanLimitExchange() {
        return new DirectExchange(appConfig.getExchange(), true, false);
    }

    @Bean
    DirectExchange notificationExchange() {
        return new DirectExchange(appConfig.getNotificationExchange(), true, false);
    }


    @Bean
    public Queue loanOverdueNotificationQueue() {
        return QueueBuilder.durable(appConfig.getLoanOverdueNotificationQueue())
                .build();
    }

    @Bean
    public Queue loanLimitQueue() {
        return QueueBuilder.durable(appConfig.getQueueName())
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(appConfig.getNotificationQueue())
                .build();
    }


    @Bean
    Binding loanLimitBinding(Queue loanLimitQueue, DirectExchange loanLimitExchange) {
        return BindingBuilder.bind(loanLimitQueue).to(loanLimitExchange).with(appConfig.getRoutingKey());
    }


    @Bean
    Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(appConfig.getNotificationRoutingKey());
    }

    @Bean
    Binding loanOverdueNotificationBinding(Queue loanOverdueNotificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(loanOverdueNotificationQueue).to(notificationExchange).with(appConfig.getLoanOverdueNotificationRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public ApplicationRunner runner(RabbitAdmin rabbitAdmin) {
        return args -> rabbitAdmin.initialize();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
