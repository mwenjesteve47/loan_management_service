package com.example.loanmanagementservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String phoneNumber;
    private Integer creditScore;
    private String firstName;
    private String lastName;
    private String nationalId;
    private String currency;
}
