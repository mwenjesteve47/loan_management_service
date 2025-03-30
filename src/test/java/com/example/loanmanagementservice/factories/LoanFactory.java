package com.example.loanmanagementservice.factories;

import com.example.loanmanagementservice.enums.BillingCycleType;
import com.example.loanmanagementservice.enums.LoanState;
import com.example.loanmanagementservice.enums.LoanStructure;
import com.example.loanmanagementservice.model.Customer;
import com.example.loanmanagementservice.model.Loan;
import com.example.loanmanagementservice.model.LoanLimit;
import com.example.loanmanagementservice.model.LoanProduct;
import com.example.loanmanagementservice.repository.LoanLimitRepository;
import com.example.loanmanagementservice.repository.LoanRepository;
import com.example.loanmanagementservice.testHelpers.SpringContext;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanFactory {
    public static Loan create(Customer customer, LoanProduct loanProduct) {
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanProduct(loanProduct);
        loan.setState(LoanState.OPEN);
        loan.setLoanStructure(LoanStructure.INSTALLMENTS);
        loan.setBillingCycleType(BillingCycleType.INDIVIDUAL_DUE_DATE);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());
        loan.setAmount(BigDecimal.valueOf(100));
        loan.setActive(1);
        loan.setAmountDue(BigDecimal.valueOf(1000));
        loan.setDueDate(LocalDate.now().plusDays(60));
        loan.setStartDate(LocalDate.now());

        return SpringContext.getBean(LoanRepository.class).save(loan);
    }

    public static void deleteAll() {
        SpringContext.getBean(LoanRepository.class).deleteAll();
    }
}
