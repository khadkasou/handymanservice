package com.nepbrain.technology.payment.controller;
import com.nepbrain.technology.payment.annotation.CustomPaymentRestController;
import com.nepbrain.technology.payment.payload.request.*;
import com.nepbrain.technology.payment.service.PaymentService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@CustomPaymentRestController @RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment, wallet and bank APIs")
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping("/save-payment") public ResponseEntity<GlobalResponse> savePayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SAVE_PAYMENT", paymentService.savePayment(request)));
    }
    @GetMapping("/payment-list") public ResponseEntity<GlobalResponse> paymentList(@RequestParam("booking_id") UUID bookingId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("PAYMENT_LIST", paymentService.paymentList(bookingId)));
    }
    @GetMapping("/payment-gateways") public ResponseEntity<GlobalResponse> paymentGateways() {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("PAYMENT_GATEWAYS", paymentService.paymentGateways()));
    }
    @GetMapping("/user-wallet-balance") public ResponseEntity<GlobalResponse> userWalletBalance(@RequestParam(value = "user_id", required = false) UUID userId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("WALLET_BALANCE", paymentService.userWalletBalance(userId)));
    }
    @GetMapping("/wallet-history") public ResponseEntity<GlobalResponse> walletHistory(@RequestParam(value = "user_id", required = false) UUID userId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("WALLET_HISTORY", paymentService.walletHistory(userId)));
    }
    @PostMapping("/wallet-top-up") public ResponseEntity<GlobalResponse> walletTopUp(@RequestBody WalletTopUpRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("WALLET_TOP_UP", paymentService.walletTopUp(request)));
    }
    @PostMapping("/withdraw-money") public ResponseEntity<GlobalResponse> withdrawMoney(@RequestBody WithdrawRequest request) {
        paymentService.withdrawMoney(request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("WITHDRAW_MONEY", "Withdraw request submitted"));
    }
    @PostMapping("/save-bank") public ResponseEntity<GlobalResponse> saveBank(@RequestBody BankRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SAVE_BANK", paymentService.saveBank(request)));
    }
    @GetMapping("/user-bank-detail") public ResponseEntity<GlobalResponse> userBankDetail(@RequestParam("user_id") UUID userId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("USER_BANK_DETAIL", paymentService.userBankDetail(userId)));
    }
    @PostMapping("/default-bank") public ResponseEntity<GlobalResponse> defaultBank(@RequestParam UUID id) {
        paymentService.defaultBank(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("DEFAULT_BANK", "Default bank updated"));
    }
    @PostMapping("/delete-bank/{bankId}") public ResponseEntity<GlobalResponse> deleteBank(@PathVariable UUID bankId) {
        paymentService.deleteBank(bankId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("DELETE_BANK", "Bank deleted"));
    }
}
