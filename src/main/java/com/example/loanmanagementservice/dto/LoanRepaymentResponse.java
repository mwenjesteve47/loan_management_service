package com.example.loanmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepaymentResponse {
    private String loanName;
    private BigDecimal remainingAmount;
}
