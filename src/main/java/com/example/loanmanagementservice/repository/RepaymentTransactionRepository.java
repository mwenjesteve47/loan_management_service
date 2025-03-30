package com.example.loanmanagementservice.repository;

import com.example.loanmanagementservice.model.RepaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepaymentTransactionRepository extends JpaRepository<RepaymentTransaction, Long> {
}
