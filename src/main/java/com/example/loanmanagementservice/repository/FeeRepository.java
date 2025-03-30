package com.example.loanmanagementservice.repository;

import com.example.loanmanagementservice.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByLoanProductId(Long loanProductID);
}
