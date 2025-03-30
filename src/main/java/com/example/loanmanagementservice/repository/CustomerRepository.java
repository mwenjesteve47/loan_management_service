package com.example.loanmanagementservice.repository;

import com.example.loanmanagementservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
