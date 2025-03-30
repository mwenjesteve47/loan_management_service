package com.example.loanmanagementservice.factories;


import com.example.loanmanagementservice.enums.TenureType;
import com.example.loanmanagementservice.enums.TenureUnit;
import com.example.loanmanagementservice.model.LoanProduct;
import com.example.loanmanagementservice.repository.LoanProductRepository;
import com.example.loanmanagementservice.testHelpers.SpringContext;

import java.time.LocalDateTime;

public class LoanProductFactory {
    public static LoanProduct create() {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setDescription("description");
        loanProduct.setName("Mortgage Loan");
        loanProduct.setTenureType(TenureType.fromValue("fixed"));
        loanProduct.setTenureUnit(TenureUnit.fromValue("Months"));
        loanProduct.setTenureValue(1);

        return SpringContext.getBean(LoanProductRepository.class).save(loanProduct);
    }
    public static LoanProduct create(Integer active) {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setDescription("description");
        loanProduct.setName("Mortgage Loan");
        loanProduct.setTenureType(TenureType.fromValue("fixed"));
        loanProduct.setTenureUnit(TenureUnit.fromValue("Months"));
        loanProduct.setTenureValue(1);

        return SpringContext.getBean(LoanProductRepository.class).save(loanProduct);
    }

    public static void deleteAll() {
        SpringContext.getBean(LoanProductRepository.class).deleteAll();
    }
}
