package com.example.loanmanagementservice.factories;


import com.example.loanmanagementservice.model.Customer;
import com.example.loanmanagementservice.repository.CustomerRepository;
import com.example.loanmanagementservice.testHelpers.SpringContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerFactory {
    public static Customer create() {
        Customer customer = new Customer();
        customer.setLastName("Doe");
        customer.setFirstName("John");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("0705238260");
        customer.setCurrency("KES");
        customer.setNationalId("12345");

        return SpringContext.getBean(CustomerRepository.class).save(customer);
    }

    public static void deleteAll() {
        SpringContext.getBean(CustomerRepository.class).deleteAll();
    }
}
