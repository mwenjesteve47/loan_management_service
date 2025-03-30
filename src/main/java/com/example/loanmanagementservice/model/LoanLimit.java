package com.example.loanmanagementservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_limits")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private String currency;

}
