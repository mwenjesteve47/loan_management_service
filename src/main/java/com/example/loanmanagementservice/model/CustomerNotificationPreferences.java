package com.example.loanmanagementservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "customer_notification_preferences")
public class CustomerNotificationPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerPreferenceId;
    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;
    private boolean smsEnabled;
    private boolean emailEnabled;
    private boolean pushNotificationEnabled;
}
