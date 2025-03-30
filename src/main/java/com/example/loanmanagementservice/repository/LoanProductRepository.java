package com.example.loanmanagementservice.repository;

import com.example.loanmanagementservice.model.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanProductRepository extends JpaRepository<LoanProduct,Long> {
}
