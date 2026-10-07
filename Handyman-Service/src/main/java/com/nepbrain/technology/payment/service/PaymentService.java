package com.nepbrain.technology.payment.service;
import com.nepbrain.technology.payment.payload.request.*;
import com.nepbrain.technology.payment.payload.response.*;
import java.util.List; import java.util.UUID;
public interface PaymentService {
    PaymentResponse savePayment(PaymentRequest request);
    List<PaymentResponse> paymentList(UUID bookingId);
    List<PaymentGatewayResponse> paymentGateways();
    WalletResponse userWalletBalance(UUID userId);
    List<WalletTransactionResponse> walletHistory(UUID userId);
    WalletResponse walletTopUp(WalletTopUpRequest request);
    void withdrawMoney(WithdrawRequest request);
    BankResponse saveBank(BankRequest request);
    List<BankResponse> userBankDetail(UUID userId);
    void defaultBank(UUID bankId);
    void deleteBank(UUID bankId);
}
