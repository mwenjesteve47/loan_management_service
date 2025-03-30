package com.example.loanmanagementservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "repayment_transactions")
public class RepaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
