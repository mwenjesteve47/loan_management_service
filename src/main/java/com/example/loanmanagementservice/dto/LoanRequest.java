package com.example.loanmanagementservice.dto;

import com.example.loanmanagementservice.enums.LoanStructure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanRequest {
    @NotNull(message = "customer id cannot be null")
    private Long customerId;
    @NotNull(message = "loan product id cannot be null")
    private Long loanProductId;
    @NotNull(message = "amount cannot be null")
    private BigDecimal amount;
    @NotBlank(message = "loanStructure cannot be blank")
    private String loanStructure;
    @NotBlank(message = "billingCycle cannot be blank")
    private String billingCycle;
    private LocalDate startDate;
    private LocalDate dueDate;
}
