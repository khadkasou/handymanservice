package com.nepbrain.technology.payment.controller;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.payment.annotation.CustomPaymentRestController;
import com.nepbrain.technology.payment.payload.request.BankRequest;
import com.nepbrain.technology.payment.payload.request.PaymentRequest;
import com.nepbrain.technology.payment.payload.request.WalletTopUpRequest;
import com.nepbrain.technology.payment.payload.request.WithdrawRequest;
import com.nepbrain.technology.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@CustomPaymentRestController
@RequiredArgsConstructor
@Tag(name = "Payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/save-payment")
    @Operation(summary = "Save payment")
    public ResponseEntity<GlobalResponse> savePayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok().body(paymentService.savePayment(request));
    }

    @GetMapping("/payment-list")
    @Operation(summary = "Payment list")
    public ResponseEntity<GlobalResponse> paymentList(@RequestParam("booking_id") UUID bookingId) {
        return ResponseEntity.ok().body(paymentService.paymentList(bookingId));
    }

    @GetMapping("/payment-gateways")
    @Operation(summary = "Payment gateways")
    public ResponseEntity<GlobalResponse> paymentGateways() {
        return ResponseEntity.ok().body(paymentService.paymentGateways());
    }

    @GetMapping("/user-wallet-balance")
    @Operation(summary = "Wallet balance")
    public ResponseEntity<GlobalResponse> userWalletBalance(@RequestParam(value = "user_id", required = false) UUID userId) {
        return ResponseEntity.ok().body(paymentService.userWalletBalance(userId));
    }

    @GetMapping("/wallet-history")
    @Operation(summary = "Wallet history")
    public ResponseEntity<GlobalResponse> walletHistory(@RequestParam(value = "user_id", required = false) UUID userId) {
        return ResponseEntity.ok().body(paymentService.walletHistory(userId));
    }

    @PostMapping("/wallet-top-up")
    @Operation(summary = "Wallet top up")
    public ResponseEntity<GlobalResponse> walletTopUp(@RequestBody WalletTopUpRequest request) {
        return ResponseEntity.ok().body(paymentService.walletTopUp(request));
    }

    @PostMapping("/withdraw-money")
    @Operation(summary = "Withdraw money")
    public ResponseEntity<GlobalResponse> withdrawMoney(@RequestBody WithdrawRequest request) {
        return ResponseEntity.ok().body(paymentService.withdrawMoney(request));
    }

    @PostMapping("/save-bank")
    @Operation(summary = "Save bank")
    public ResponseEntity<GlobalResponse> saveBank(@RequestBody BankRequest request) {
        return ResponseEntity.ok().body(paymentService.saveBank(request));
    }

    @GetMapping("/user-bank-detail")
    @Operation(summary = "User bank detail")
    public ResponseEntity<GlobalResponse> userBankDetail(@RequestParam("user_id") UUID userId) {
        return ResponseEntity.ok().body(paymentService.userBankDetail(userId));
    }

    @PostMapping("/default-bank")
    @Operation(summary = "Set default bank")
    public ResponseEntity<GlobalResponse> defaultBank(@RequestParam UUID id) {
        return ResponseEntity.ok().body(paymentService.defaultBank(id));
    }

    @PutMapping("/bank/{bankId}/status")
    @Operation(summary = "Change bank status")
    public ResponseEntity<GlobalResponse> changeBankStatus(@PathVariable UUID bankId, @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(paymentService.changeBankStatus(bankId, status));
    }

    @PostMapping("/delete-bank/{bankId}")
    @Operation(summary = "Mark bank inactive")
    public ResponseEntity<GlobalResponse> deleteBank(@PathVariable UUID bankId) {
        return ResponseEntity.ok().body(paymentService.deleteBank(bankId));
    }
}
