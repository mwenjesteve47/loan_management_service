package com.example.loanmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanOverdueEventPayload {
    private Long customerId;
    private Long loanId;
    private BigDecimal overdueAmount;
    private LocalDate dueDate;
}
