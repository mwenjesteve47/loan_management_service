package com.example.loanmanagementservice.factories;

import com.example.loanmanagementservice.model.Customer;
import com.example.loanmanagementservice.model.LoanLimit;
import com.example.loanmanagementservice.repository.LoanLimitRepository;
import com.example.loanmanagementservice.testHelpers.SpringContext;

import java.math.BigDecimal;

public class LoanLimitFactory {
    public static LoanLimit create(Customer customer) {
        LoanLimit loanLimit = new LoanLimit();
        loanLimit.setCreditLimit(BigDecimal.valueOf(1000));
        loanLimit.setAvailableLimit(BigDecimal.valueOf(1000));
        loanLimit.setCurrency("KES");
        loanLimit.setCustomer(customer);
        return SpringContext.getBean(LoanLimitRepository.class).save(loanLimit);
    }

    public static void deleteAll() {
        SpringContext.getBean(LoanLimitRepository.class).deleteAll();
    }
}
