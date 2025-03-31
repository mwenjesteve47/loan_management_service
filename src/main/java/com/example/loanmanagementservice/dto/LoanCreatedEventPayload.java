package com.example.loanmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanCreatedEventPayload {
    private Long customerId;
    private Long loanId;
    private String loanName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String loanState;
    private LocalDateTime createdAt;
    private Integer active;
}
