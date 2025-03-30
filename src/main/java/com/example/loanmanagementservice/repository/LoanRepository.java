package com.example.loanmanagementservice.repository;

import com.example.loanmanagementservice.enums.LoanState;
import com.example.loanmanagementservice.model.Customer;
import com.example.loanmanagementservice.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByDueDateBeforeAndState(LocalDate now, LoanState loanState);

    @Query("SELECT l FROM Loan l WHERE l.customer.id = :customerId AND l.state = :state")
    List<Loan> findByCustomerIdAndState(@Param("customerId") Long customerId, @Param("state") LoanState state);

    List<Loan> findByState(LoanState loanState);


    @Query("""
     SELECT l FROM Loan l
     WHERE (:loanProductId IS NULL OR l.loanProduct.id = :loanProductId)
     AND (:customerId IS NULL OR l.customer.id = :customerId)
     AND (:active IS NULL OR l.active = :active)
     AND (:loanId IS NULL OR l.id = :loanId)
    """)
    Page<Loan> getLoans( @Param("loanProductId") Long loanProductId,
                             @Param("customerId") Long customerId,
                             @Param("active") Integer active,
                             @Param("loanId") Integer loanId,
                             Pageable pageable);;
}
