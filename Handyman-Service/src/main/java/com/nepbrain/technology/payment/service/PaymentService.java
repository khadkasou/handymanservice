package com.nepbrain.technology.payment.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.payment.payload.request.*;

import java.util.UUID;

public interface PaymentService {

    GlobalResponse savePayment(PaymentRequest request);

    GlobalResponse paymentList(UUID bookingId);

    GlobalResponse paymentGateways();

    GlobalResponse userWalletBalance(UUID userId);

    GlobalResponse walletHistory(UUID userId);

    GlobalResponse walletTopUp(WalletTopUpRequest request);

    GlobalResponse withdrawMoney(WithdrawRequest request);

    GlobalResponse saveBank(BankRequest request);

    GlobalResponse userBankDetail(UUID userId);

    GlobalResponse defaultBank(UUID bankId);

    GlobalResponse changeBankStatus(UUID bankId, StatusEnum status);

    GlobalResponse deleteBank(UUID bankId);
}
