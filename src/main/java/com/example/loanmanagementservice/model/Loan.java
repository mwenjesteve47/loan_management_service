package com.example.loanmanagementservice.model;

import com.example.loanmanagementservice.enums.BillingCycleType;
import com.example.loanmanagementservice.enums.LoanState;
import com.example.loanmanagementservice.enums.LoanStructure;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;

    private BigDecimal amount;
    private BigDecimal amountDue;
    private LocalDate startDate;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private LoanStructure loanStructure;
    @Enumerated(EnumType.STRING)
    private BillingCycleType billingCycleType;
    @Enumerated(EnumType.STRING)
    private LoanState state; // OPEN, CLOSED, OVERDUE, WRITTEN_OFF
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer active;
}
