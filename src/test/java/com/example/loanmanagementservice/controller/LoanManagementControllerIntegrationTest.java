package com.example.loanmanagementservice.controller;

import com.example.loanmanagementservice.AbstractIntegrationTest;
import com.example.loanmanagementservice.configs.AppConfig;
import com.example.loanmanagementservice.dto.LoanRepaymentRequest;
import com.example.loanmanagementservice.dto.LoanRequest;
import com.example.loanmanagementservice.enums.BillingCycleType;
import com.example.loanmanagementservice.enums.LoanStructure;
import com.example.loanmanagementservice.factories.*;
import com.example.loanmanagementservice.model.Loan;
import com.example.loanmanagementservice.repository.LoanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.loanmanagementservice.enums.LoanState.CLOSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
public class LoanManagementControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    RabbitTemplate defaultRabbitTemplate;

    @Autowired
    AppConfig appConfig;


    @BeforeEach
    void preCleanUp() {
        RepaymentTransactionFactory.deleteAll();
        CustomerNotificationPreferencesFactory.deleteAll();
        LoanLimitFactory.deleteAll();
        LoanFactory.deleteAll();
        CustomerFactory.deleteAll();
    }

    @Test
    void givenValidFilterRequest_whenFetchingLoans_thenReturnLoansSuccessfully() throws Exception {
        // Given: Some loan products exist
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var loanProduct = LoanProductFactory.create();
        LoanFactory.create(customer,loanProduct);

        mockMvc.perform(MockMvcRequestBuilders.get("/loans")
                        .param("page", "1")
                        .param("size", "10")
                        .param("loanProductId", loanProduct.getId().toString())
                        .param("customerId", customer.getId().toString())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Loan retrieved"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data.content.[0].state").value("OPEN"))
                .andExpect(jsonPath("$.data.data.content.[0].amountDue").value(1000.00));
    }


    @Test
    void givenExistentLoanID_whenUpdatingLoanState_thenReturnSuccess() throws Exception {
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var loanProduct = LoanProductFactory.create();
        var loan = LoanFactory.create(customer,loanProduct);

        mockMvc.perform(patch("/loans/"+loan.getId()+"/state")
                        .param("newState", "CLOSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldProcessLoanRepaymentAndPublishEvent(CapturedOutput output) throws Exception {
        // Given: A loan with an outstanding balance of 1000.00 exists
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var loanProduct = LoanProductFactory.create();
        var loan = LoanFactory.create(customer,loanProduct);

        // When: The customer makes a repayment of 500.00
        LoanRepaymentRequest request = LoanRepaymentRequest.builder().amountPaid(BigDecimal.valueOf(500)).build();

        mockMvc.perform(post("/loans/" + loan.getId() + "/repay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Loan repayment processed successfully"))
                .andExpect(jsonPath("$.data.remainingAmount").value(500.00));

        // Then: The loan amount due should be updated to 500.00
        Loan updatedLoan = loanRepository.findById(loan.getId()).orElseThrow();
        assertThat(updatedLoan.getAmountDue()).isEqualByComparingTo("500.00");

        // And: A RabbitMQ event should be published
        awaitQueueIsNotEmpty(defaultRabbitTemplate, "loan.limit.queue", 20);
    }

    @Test
    void shouldRejectInvalidRepaymentAmount() throws Exception {
        // Given: A loan with an outstanding balance of 1000.00 exists
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var loanProduct = LoanProductFactory.create();
        var loan = LoanFactory.create(customer,loanProduct);
        // When: The customer tries to make a repayment with a negative amount
        LoanRepaymentRequest invalidRequest = LoanRepaymentRequest.builder().amountPaid(BigDecimal.valueOf(-500)).build();

        mockMvc.perform(post("/loans/" + loan.getId() + "/repay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Then: The loan amount due should remain unchanged
        Loan unchangedLoan = loanRepository.findById(loan.getId()).orElseThrow();
        assertThat(unchangedLoan.getAmountDue()).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldCloseLoanOnFullRepayment(CapturedOutput capturedOutput) throws Exception {
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var loanProduct = LoanProductFactory.create();
        var loan = LoanFactory.create(customer,loanProduct);
        // When: The customer makes a full repayment of 1000.00
        LoanRepaymentRequest fullRepaymentRequest = LoanRepaymentRequest.builder().amountPaid(BigDecimal.valueOf(1000)).build();

        mockMvc.perform(post("/loans/" + loan.getId() + "/repay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullRepaymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.remainingAmount").value(0.00));

        // Then: The loan should be fully repaid and marked as CLOSED
        Loan closedLoan = loanRepository.findById(loan.getId()).orElseThrow();
        assertThat(closedLoan.getAmountDue()).isEqualByComparingTo("0.00");
        assertThat(closedLoan.getState()).isEqualTo(CLOSED);

        // And: A RabbitMQ event should be published
        awaitQueueIsNotEmpty(defaultRabbitTemplate, "loan.limit.queue", 20);
    }

    @Test
    void givenValidLoanRequest_whenCreatingLoan_thenReturnSuccess() throws Exception {
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var loanProduct = LoanProductFactory.create();
        FeeFactory.create(loanProduct);

        // Given
        var request = LoanRequest.builder()
                .customerId(customer.getId())
                .loanProductId(loanProduct.getId())
                .amount(BigDecimal.valueOf(100))
                .loanStructure(LoanStructure.LUMP_SUM.name())
                .billingCycle(BillingCycleType.INDIVIDUAL_DUE_DATE.getValue())
                .startDate(LocalDate.now())
                .dueDate(LocalDate.now())
                .build();


        // When & Then
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Loan created successfully"))
                .andExpect(jsonPath("$.data.customerName").value("John Doe"))
                .andExpect(jsonPath("$.data.amountDue").value(1100.00));

        awaitQueueIsNotEmpty(defaultRabbitTemplate, "loan.notification.queue", 20);

    }

}
