package com.example.loanmanagementservice.model;

import com.example.loanmanagementservice.enums.FeeCalculationType;
import com.example.loanmanagementservice.enums.FeeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "fees")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanProduct loanProduct;

    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    private FeeCalculationType calculationType;

    private BigDecimal amount;

    private Integer daysAfterDue;

}
