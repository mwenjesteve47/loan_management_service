package com.example.loanmanagementservice.factories;


import com.example.loanmanagementservice.model.Customer;
import com.example.loanmanagementservice.model.CustomerNotificationPreferences;
import com.example.loanmanagementservice.repository.CustomerNotificationPreferenceRepository;
import com.example.loanmanagementservice.testHelpers.SpringContext;

public class CustomerNotificationPreferencesFactory {

    public static CustomerNotificationPreferences create(Customer customer) {
        CustomerNotificationPreferences customerNotificationPreferences = new CustomerNotificationPreferences();
        customerNotificationPreferences.setCustomer(customer);
        customerNotificationPreferences.setEmailEnabled(Boolean.TRUE);
        customerNotificationPreferences.setPushNotificationEnabled(Boolean.TRUE);
        customerNotificationPreferences.setSmsEnabled(Boolean.TRUE);
        return SpringContext.getBean(CustomerNotificationPreferenceRepository.class).save(customerNotificationPreferences);
    }

    public static void deleteAll() {
        SpringContext.getBean(CustomerNotificationPreferenceRepository.class).deleteAll();
    }
}
