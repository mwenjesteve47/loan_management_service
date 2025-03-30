package com.example.loanmanagementservice.repository;

import com.example.loanmanagementservice.model.LoanLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface LoanLimitRepository extends JpaRepository<LoanLimit, Integer> {
   Optional<LoanLimit> findByCustomerId(Long id);

}
