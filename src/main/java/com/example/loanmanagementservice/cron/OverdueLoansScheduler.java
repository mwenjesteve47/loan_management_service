package com.example.loanmanagementservice.cron;

import com.example.loanmanagementservice.configs.AppConfig;
import com.example.loanmanagementservice.dto.LoanLimitAdjustmentEventDto;
import com.example.loanmanagementservice.enums.LoanState;
import com.example.loanmanagementservice.helper.LoanHelper;
import com.example.loanmanagementservice.model.Loan;
import com.example.loanmanagementservice.repository.LoanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OverdueLoansScheduler {
    private final LoanRepository loanRepository;
    private final LoanHelper loanHelper;
    private final AppConfig appConfig;

    @Scheduled(cron = "#{@appConfig.getCronExpression()}")

    public void applyDailyFees() throws JsonProcessingException {
        log.info("RUNNING SWEEPING JOB");
        List<Loan> activeLoans = loanRepository.findByState(LoanState.OPEN);

        for (Loan loan : activeLoans) {
            //apply Lateness fee for all open loans that are overdue
            loanHelper.applyLatenessPenalty(loan);
            // Publish event for loan limit reduction
            var loanLimitAdjustmentEvent = LoanLimitAdjustmentEventDto.builder()
                    .adjustment("subtraction")
                    .customerId(loan.getCustomer().getId())
                    .loanId(loan.getId())
                    .build();
            loanRepository.save(loan);
            log.info("PUBLISHING TO LOAN LIMIT ADJUSTMENT QUEUE {}", appConfig.getQueueName());
            loanHelper.sendLoanLimitAdjustmentEvent(loanLimitAdjustmentEvent);
            log.info("PUBLISHING TO LOAN OVERDUE NOTIFICATION QUEUE {}", appConfig.getLoanOverdueNotificationQueue());
            loanHelper.sendLoanOverdueNotification(loan);
        }
    }
}
