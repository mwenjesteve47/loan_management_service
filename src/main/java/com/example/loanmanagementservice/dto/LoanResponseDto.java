package com.example.loanmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {
    private Long id;

    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;
    private String customerNationalId;

    private String loanProductName;
    private String loanProductDescription;
    private String tenureType;
    private Integer tenureValue;
    private String tenureUnit;

    private String currency;
    private BigDecimal amount;
    private BigDecimal amountDue;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String loanStructure;
    private String billingCycleType;
    private String state;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer active;
}
