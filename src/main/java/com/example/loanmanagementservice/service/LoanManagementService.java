package com.example.loanmanagementservice.service;


import com.example.loanmanagementservice.configs.AppConfig;
import com.example.loanmanagementservice.configs.RabbitMQConfig;
import com.example.loanmanagementservice.dto.*;
import com.example.loanmanagementservice.enums.*;
import com.example.loanmanagementservice.exception.ResourceNotFoundException;
import com.example.loanmanagementservice.helper.LoanHelper;
import com.example.loanmanagementservice.model.*;
import com.example.loanmanagementservice.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanManagementService {

    private final LoanRepository loanRepository;
    private final FeeRepository feeRepository;
    private final LoanProductRepository loanProductRepository;
    private final CustomerRepository customerRepository;
    private final RepaymentTransactionRepository repaymentTransactionRepository;
    private final LoanHelper loanHelper;
    private final RabbitTemplate rabbitTemplate;
    private final AppConfig appConfig;
    private final LoanLimitRepository loanLimitRepository;

    public static final String SORT_BY_DATE_CREATED = "createdAt";


    public LoanResponseDto createLoan(LoanRequest request) {
        LoanProduct loanProduct = fetchLoanProduct(request.getLoanProductId());
        Customer customer = fetchCustomer(request.getCustomerId());

        validateLoanLimit(request.getAmount(), customer);

        if (isIndividualDueDateBilling(request.getBillingCycle())) {
            validateAndSetIndividualDueDate(request, loanProduct);
        } else {
            validateAndSetConsolidatedDueDate(request, customer);
        }

        BigDecimal serviceFee = fetchServiceFee(loanProduct.getId());

        Loan loan = createLoanEntity(request, customer, loanProduct, serviceFee);
        Loan savedLoan = loanRepository.save(loan);

        loanHelper.publishLoanCreatedEvent(savedLoan, customer);

        return buildLoanResponse(savedLoan);
    }

    private LoanResponseDto buildLoanResponse(Loan loan) {
        return LoanResponseDto.builder()
                .id(loan.getId())
                .customerEmail(loan.getCustomer().getEmail())
                .customerPhoneNumber(loan.getCustomer().getPhoneNumber())
                .customerNationalId(loan.getCustomer().getNationalId())
                .customerName(loan.getCustomer().getFirstName() + " " + loan.getCustomer().getLastName())
                .loanProductName(loan.getLoanProduct().getName())
                .loanProductDescription(loan.getLoanProduct().getDescription())
                .tenureType(loan.getLoanProduct().getTenureType().name())
                .tenureValue(loan.getLoanProduct().getTenureValue())
                .tenureUnit(loan.getLoanProduct().getTenureUnit().name())
                .currency(loan.getCustomer().getCurrency())
                .amount(loan.getAmount())
                .amountDue(loan.getAmountDue())
                .startDate(loan.getStartDate())
                .dueDate(loan.getDueDate())
                .loanStructure(loan.getLoanStructure().name())
                .billingCycleType(loan.getBillingCycleType().name())
                .state(loan.getState().name())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .active(loan.getActive())
                .build();
    }


    private Loan createLoanEntity(LoanRequest request, Customer customer, LoanProduct loanProduct, BigDecimal serviceFee) {
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanProduct(loanProduct);
        loan.setAmount(request.getAmount());
        loan.setLoanStructure(LoanStructure.fromValue(request.getLoanStructure()));
        loan.setStartDate(LocalDate.now());
        loan.setState(LoanState.OPEN);
        loan.setBillingCycleType(BillingCycleType.fromValue(request.getBillingCycle()));
        loan.setAmountDue(request.getAmount().add(serviceFee));
        loan.setCreatedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());
        loan.setActive(1);
        return loan;
    }


    private BigDecimal fetchServiceFee(Long loanProductId) {
        return feeRepository.findByLoanProductId(loanProductId)
                .stream()
                .filter(Objects::nonNull)
                .filter(fee -> FeeType.SERVICE_FEE.equals(fee.getFeeType()))
                .map(Fee::getAmount)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Loan product does not have a service fee"));
    }

    private boolean isIndividualDueDateBilling(String billingCycle) {
        return BillingCycleType.INDIVIDUAL_DUE_DATE.getValue().equalsIgnoreCase(billingCycle);
    }

    private void validateLoanLimit(BigDecimal requestedAmount, Customer customer) {
        var loanLimit = loanLimitRepository.findByCustomerId(customer.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Loan limit does not exist for the customer: " + customer.getLastName()));

        BigDecimal availableLimit = loanLimit.getAvailableLimit();
        BigDecimal creditLimit = loanLimit.getCreditLimit();

        if (availableLimit.compareTo(BigDecimal.ZERO) == 0) {
            loanLimit.setAvailableLimit(creditLimit);
            loanLimitRepository.save(loanLimit);
            throw new IllegalArgumentException("Your loan limit was updated to " + creditLimit + ". Please retry with the new limit.");
        }

        if (requestedAmount.compareTo(availableLimit) > 0) {
            throw new IllegalArgumentException("Loan request amount exceeds your loan limit of " + loanLimit);
        }
    }


    private LoanProduct fetchLoanProduct(Long loanProductId) {
        return loanProductRepository.findById(loanProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));
    }

    private Customer fetchCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }


    private void validateAndSetIndividualDueDate(LoanRequest loan, LoanProduct loanProduct) {
        int tenureInDays;
        if (loanProduct.getTenureUnit() == TenureUnit.MONTHS) {
            tenureInDays = loanProduct.getTenureValue() * 30; // Approximate conversion
        } else {
            tenureInDays = loanProduct.getTenureValue(); // Already in days
        }
        LocalDate dueDate = loan.getStartDate().plusDays(tenureInDays);
        loan.setDueDate(dueDate);
    }


    private void validateAndSetConsolidatedDueDate(LoanRequest request, Customer customer) {
        List<Loan> activeLoans = loanRepository.findByCustomerIdAndState(customer.getId(), LoanState.OPEN);

        if (activeLoans.size() < 2) {
            throw new IllegalStateException("Consolidated billing cycle requires at least two active loans.");
        }

        // Find the latest due date among existing loans
        LocalDate latestDueDate = activeLoans.stream()
                .map(Loan::getDueDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null); // If no valid due dates exist, return null

        LocalDate requestedDueDate = request.getDueDate();

        // Determine the final due date (whichever is later)
        LocalDate finalDueDate;
        if (latestDueDate == null) {
            finalDueDate = requestedDueDate; // If no valid due date exists, use the requested date
        } else {
            finalDueDate = requestedDueDate.isAfter(latestDueDate) ? requestedDueDate : latestDueDate;
        }

        // Set the determined due date on the loan
        request.setDueDate(finalDueDate);
    }


    @Transactional
    public void updateLoanState(Long loanId, LoanState newState) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        LoanHelper.transitionLoanState(loan, newState);
        loan.setUpdatedAt(LocalDateTime.now());
        loanRepository.save(loan);
    }


    @Transactional
    public LoanRepaymentResponse processLoanRepayment(Long loanId, BigDecimal amountPaid) throws JsonProcessingException {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Repayment amount must be greater than zero");
        }

        BigDecimal remainingBalance = loan.getAmountDue().subtract(amountPaid);

        if (remainingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Repayment amount exceeds outstanding loan balance");
        }

        // Update loan amount due
        loan.setAmountDue(remainingBalance);

        // If fully repaid, close the loan
        if (remainingBalance.compareTo(BigDecimal.ZERO) == 0) {
            LoanHelper.transitionLoanState(loan, LoanState.CLOSED);
        }

        loan.setUpdatedAt(LocalDateTime.now());
        loanRepository.save(loan);

        var loanLimitAdjustmentEvent = LoanLimitAdjustmentEventDto.builder()
                .adjustment("addition")
                .customerId(loan.getCustomer().getId())
                .loanId(loan.getId())
                .build();
        //send message to customer service for loan adjustment
        loanHelper.sendLoanLimitAdjustmentEvent(loanLimitAdjustmentEvent);
        //send a loan repayment ack to notification service

        // Log the repayment transaction
        logRepaymentTransaction(loan, amountPaid);
        log.info("Loan repayment with id {} has been processed", loan.getId());
        return new LoanRepaymentResponse(loan.getLoanProduct().getName(), remainingBalance);
    }

    private void logRepaymentTransaction(Loan loan, BigDecimal amountPaid) {
        RepaymentTransaction transaction = new RepaymentTransaction();
        transaction.setLoan(loan);
        transaction.setAmountPaid(amountPaid);
        transaction.setPaymentDate(LocalDateTime.now());
        transaction.setCustomer(loan.getCustomer());

        repaymentTransactionRepository.save(transaction);
    }

    public ApiResponse<?> getLoans(LoanFilterRequest request) {

        Sort sort = Sort.by(SORT_BY_DATE_CREATED).descending();
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

        // Log query parameters
        log.info("Fetching loan products with filters - loanProductID: {}, customerID: {}, Active: {}, ID: {}, Page: {}, Size: {}",
                request.getLoanProductId(),
                request.getCustomerId(),
                request.getActive(),
                request.getId(),
                request.getPage(),
                request.getSize()
        );

        // Filter
        Page<Loan> loanPage = loanRepository.getLoans(
                request.getLoanProductId(),
                request.getCustomerId(),
                1,
                request.getId(),
                pageable
        );

        // Transform Loan entities into DTOs
        List<LoanResponseDto> loanResponses = loanPage.getContent().stream()
                .map(loan -> LoanResponseDto.builder()
                        .id(loan.getId())
                        .customerId(loan.getCustomer().getId())
                        .customerName(loan.getCustomer().getFirstName()+ " " +loan.getCustomer().getLastName())
                        .customerEmail(loan.getCustomer().getEmail())
                        .customerPhoneNumber(loan.getCustomer().getPhoneNumber())
                        .customerNationalId(loan.getCustomer().getNationalId())
                        .loanProductName(loan.getLoanProduct().getName())
                        .loanProductDescription(loan.getLoanProduct().getDescription())
                        .tenureType(loan.getLoanProduct().getTenureType().name())
                        .tenureValue(loan.getLoanProduct().getTenureValue())
                        .tenureUnit(loan.getLoanProduct().getTenureUnit().name())

                        .currency(loan.getCustomer().getCurrency())
                        .amount(loan.getAmount())
                        .amountDue(loan.getAmountDue())
                        .startDate(loan.getStartDate())
                        .dueDate(loan.getDueDate())
                        .loanStructure(loan.getLoanStructure().name())
                        .billingCycleType(loan.getBillingCycleType().name())
                        .state(loan.getState().name())

                        .createdAt(loan.getCreatedAt())
                        .updatedAt(loan.getUpdatedAt())
                        .active(loan.getActive())
                        .build())
                .collect(Collectors.toList());

        return new ApiResponse<>(true, "Loans retrieved successfully", new PaginateObjectResponse<>(
                loanResponses,
                loanPage.getNumber(),
                loanPage.getSize(),
                loanPage.getTotalElements(),
                loanPage.getTotalPages(),
                loanPage.isLast()));


    }
}
