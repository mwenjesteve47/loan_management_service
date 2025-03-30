/**
 * Author: Kevin Mkenya
 * User:kevin.mkenya
 * Date:11/09/2024
 * Time:3:37 PM
 */

package com.example.loanmanagementservice.testHelpers;

import com.example.loanmanagementservice.configs.AppConfig;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMqTestHelper {

    public static void createLoanLimitQueue(AppConfig appConfig, RabbitTemplate rabbitTemplate)
    {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());
        rabbitAdmin.declareQueue(QueueBuilder.durable(appConfig.getQueueName()).build());
    }
}
