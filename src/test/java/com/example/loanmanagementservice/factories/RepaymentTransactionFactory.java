package com.example.loanmanagementservice.factories;

import com.example.loanmanagementservice.enums.TenureType;
import com.example.loanmanagementservice.enums.TenureUnit;
import com.example.loanmanagementservice.model.Customer;
import com.example.loanmanagementservice.model.Loan;
import com.example.loanmanagementservice.model.LoanProduct;
import com.example.loanmanagementservice.model.RepaymentTransaction;
import com.example.loanmanagementservice.repository.LoanProductRepository;
import com.example.loanmanagementservice.repository.RepaymentTransactionRepository;
import com.example.loanmanagementservice.testHelpers.SpringContext;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RepaymentTransactionFactory {
    public static RepaymentTransaction create(Customer customer,Loan loan) {
        RepaymentTransaction repaymentTransaction = new RepaymentTransaction();
        repaymentTransaction.setCustomer(customer);
        repaymentTransaction.setLoan(loan);
        repaymentTransaction.setAmountPaid(BigDecimal.ZERO);
        repaymentTransaction.setPaymentDate(LocalDateTime.now());

        return SpringContext.getBean(RepaymentTransactionRepository.class).save(repaymentTransaction);
    }

    public static void deleteAll() {
        SpringContext.getBean(RepaymentTransactionRepository.class).deleteAll();
    }

}
