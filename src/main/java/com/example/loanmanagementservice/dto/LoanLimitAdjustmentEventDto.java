package com.example.loanmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanLimitAdjustmentEventDto {
    private Long customerId;
    private Long loanId;
    private String adjustment;
}
