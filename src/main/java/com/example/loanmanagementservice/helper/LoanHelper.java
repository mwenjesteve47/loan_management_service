package com.example.loanmanagementservice.helper;


import com.example.loanmanagementservice.configs.AppConfig;
import com.example.loanmanagementservice.configs.RabbitMQConfig;
import com.example.loanmanagementservice.dto.LoanCreatedEventPayload;
import com.example.loanmanagementservice.dto.LoanLimitAdjustmentEventDto;
import com.example.loanmanagementservice.dto.LoanOverdueEventPayload;
import com.example.loanmanagementservice.enums.FeeCalculationType;
import com.example.loanmanagementservice.enums.FeeType;
import com.example.loanmanagementservice.enums.LoanState;
import com.example.loanmanagementservice.model.Customer;
import com.example.loanmanagementservice.model.Fee;
import com.example.loanmanagementservice.model.Loan;
import com.example.loanmanagementservice.repository.FeeRepository;
import com.example.loanmanagementservice.repository.LoanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor()
@Slf4j
public class LoanHelper {

    private final LoanRepository loanRepository;
    private final FeeRepository feeRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AppConfig appConfig;


    public void sendLoanOverdueNotification(Loan loan) {
        LoanOverdueEventPayload loanOverdueEventPayload = new LoanOverdueEventPayload(
                loan.getCustomer().getId(),
                loan.getId(),
                loan.getLoanProduct().getName(),
                loan.getCustomer().getFirstName(),
                loan.getAmountDue(),
                loan.getDueDate()
        );

        rabbitTemplate.convertAndSend(appConfig.getNotificationExchange(), appConfig.getLoanOverdueNotificationRoutingKey(), loanOverdueEventPayload);

        log.info("Loan Overdue Event Published: {}", loanOverdueEventPayload);
    }
    /**
     * Handles loan state transitions based on business rules.
     *
     * @param loan The loan object to update.
     * @param newState The new state to transition to.
     */
    public static void transitionLoanState(Loan loan, LoanState newState) {
        LoanState currentState = loan.getState();

        // Validate the state transition
        if (!isValidTransition(currentState, newState)) {
            throw new IllegalStateException("Invalid loan state transition from "
                    + currentState + " to " + newState);
        }

        // Perform the transition
        loan.setState(newState);

        // Additional business logic per state
        if (newState == LoanState.CLOSED||newState == LoanState.WRITTEN_OFF) {
            loan.setUpdatedAt(LocalDateTime.now());
        }
    }

    /**
     * Validates whether a loan state transition is allowed.
     *
     * @param currentState The current loan state.
     * @param newState The desired loan state.
     * @return True if the transition is valid, otherwise false.
     */
    private static boolean isValidTransition(LoanState currentState, LoanState newState) {
        return switch (currentState) {
            case OPEN -> EnumSet.of(LoanState.OVERDUE, LoanState.CLOSED, LoanState.CANCELLED, LoanState.WRITTEN_OFF).contains(newState);
            case OVERDUE -> EnumSet.of(LoanState.CLOSED, LoanState.WRITTEN_OFF).contains(newState);
            case CLOSED, CANCELLED, WRITTEN_OFF -> false; // Cannot transition from final states
        };
    }

    private void applyDailyInterest(Loan loan) {
        BigDecimal dailyFee = feeRepository.findByLoanProductId(loan.getLoanProduct().getId())
                .stream()
                .filter(Objects::nonNull)
                .filter(fee -> FeeType.DAILY_FEE.equals(fee.getFeeType())) // Correct enum comparison
                .map(Fee::getAmount) // Assuming Fee has an `amount` field
                .findFirst() // Get the first matching fee
                .orElse(BigDecimal.ZERO); // Default to zero if no service fee is found


        if (dailyFee.compareTo(BigDecimal.ZERO) > 0) {
            loan.setAmountDue(loan.getAmountDue().add(dailyFee));
        }
    }

    public void applyLatenessPenalty(Loan loan) {
        if (loan.getDueDate().isBefore(LocalDate.now())) {
            loan.setState(LoanState.OVERDUE);

            Optional<Fee> optionalLateFee = feeRepository.findByLoanProductId(loan.getLoanProduct().getId())
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(fee -> FeeType.LATE_FEE.equals(fee.getFeeType()))
                    .findFirst();

            if (optionalLateFee.isPresent()) {
                Fee latenessFee = optionalLateFee.get();
                BigDecimal feeAmount = latenessFee.getAmount();

                if (latenessFee.getCalculationType().equals(FeeCalculationType.PERCENTAGE)) {
                    BigDecimal percentageAmount = loan.getAmountDue().multiply(feeAmount).divide(BigDecimal.valueOf(100));
                    loan.setAmountDue(loan.getAmountDue().add(percentageAmount));
                } else {
                    loan.setAmountDue(loan.getAmountDue().add(feeAmount));
                }
            }
        }
    }

    public void sendLoanLimitAdjustmentEvent(LoanLimitAdjustmentEventDto loanLimitAdjustmentEventDto) throws JsonProcessingException {

        // Convert event data to JSON
        String messageBody = new ObjectMapper().writeValueAsString(loanLimitAdjustmentEventDto);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

        Message message = new Message(messageBody.getBytes(), messageProperties);

        // Send message to RabbitMQ
        rabbitTemplate.convertAndSend(appConfig.getExchange(), appConfig.getRoutingKey(), message);

        log.info("Loan Adjustment Event Payload Published: {}, Published to Queue: {}", message, appConfig.getQueueName());

    }
    public void publishLoanCreatedEvent(Loan loan, Customer customer) {
        LoanCreatedEventPayload payload = new LoanCreatedEventPayload(
                customer.getId(),
                loan.getId(),
                loan.getLoanProduct().getName(),
                loan.getAmount(),
                loan.getDueDate(),
                loan.getState().toString(),
                loan.getCreatedAt(),
                loan.getActive()
        );

        rabbitTemplate.convertAndSend(appConfig.getNotificationExchange(), appConfig.getNotificationRoutingKey(), payload);
        log.info("Loan Created Event Published: {}, Queue: {}", payload, appConfig.getNotificationQueue());
    }

}
