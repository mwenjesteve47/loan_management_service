package com.example.loanmanagementservice.factories;



import com.example.loanmanagementservice.enums.FeeType;
import com.example.loanmanagementservice.model.Fee;
import com.example.loanmanagementservice.model.LoanProduct;
import com.example.loanmanagementservice.repository.FeeRepository;
import com.example.loanmanagementservice.testHelpers.SpringContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FeeFactory {
    public static Fee create(LoanProduct loanProduct) {
        Fee fee = new Fee();
        fee.setLoanProduct(loanProduct);
        fee.setFeeType(FeeType.SERVICE_FEE);
        fee.setAmount(BigDecimal.valueOf(1000));
        fee.setDaysAfterDue(30);

        return SpringContext.getBean(FeeRepository.class).save(fee);
    }

    public static void deleteAll() {
        SpringContext.getBean(FeeRepository.class).deleteAll();
    }
}
