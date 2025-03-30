package com.example.loanmanagementservice.controller;

import com.example.loanmanagementservice.dto.*;
import com.example.loanmanagementservice.enums.LoanState;
import com.example.loanmanagementservice.model.Loan;
import com.example.loanmanagementservice.service.LoanManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/loans")
public class LoanManagementController {
    private final LoanManagementService loanService;

    public LoanManagementController(LoanManagementService loanService) {
        this.loanService = loanService;
    }

    /**
     * Create a new loan (either lump sum or installment-based).
     */
    @PostMapping()
    public ResponseEntity<ApiResponse<LoanResponseDto>> createLoan(@Valid @RequestBody LoanRequest request) {
        var loan = loanService.createLoan(request);
        return ResponseEntity.ok(new ApiResponse<>(true,"Loan created successfully", loan));
    }


    @GetMapping
    public ResponseEntity<?> getLoans(@Valid LoanFilterRequest request) {
        var loan = loanService.getLoans(request);
        return ResponseEntity.ok(new ApiResponse<>(true,"Loan retrieved", loan));
    }

    @PatchMapping("/{loanId}/state")
    public ResponseEntity<ApiResponse<?>> updateLoanState(
            @PathVariable Long loanId,
            @RequestParam LoanState newState) {

        loanService.updateLoanState(loanId, newState);
        return ResponseEntity.ok(new ApiResponse<>(true, "Loan state updated successfully"));
    }

    @PostMapping("/{loanId}/repay")
    public ResponseEntity<ApiResponse<LoanRepaymentResponse>> repayLoan(
            @PathVariable Long loanId,
            @RequestBody LoanRepaymentRequest repaymentRequest) throws JsonProcessingException {

        LoanRepaymentResponse response = loanService.processLoanRepayment(loanId, repaymentRequest.getAmountPaid());
        return ResponseEntity.ok(new ApiResponse<>(true, "Loan repayment processed successfully", response));
    }

}
