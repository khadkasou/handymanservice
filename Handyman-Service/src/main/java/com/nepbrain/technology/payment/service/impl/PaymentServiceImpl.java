package com.nepbrain.technology.payment.service.impl;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.payment.entity.BankEntity;
import com.nepbrain.technology.payment.entity.PaymentEntity;
import com.nepbrain.technology.payment.entity.PaymentGatewayEntity;
import com.nepbrain.technology.payment.entity.WalletEntity;
import com.nepbrain.technology.payment.entity.WalletTransactionEntity;
import com.nepbrain.technology.payment.enums.ErrorCodeEnum;
import com.nepbrain.technology.payment.payload.request.BankRequest;
import com.nepbrain.technology.payment.payload.request.PaymentRequest;
import com.nepbrain.technology.payment.payload.request.WalletTopUpRequest;
import com.nepbrain.technology.payment.payload.request.WithdrawRequest;
import com.nepbrain.technology.payment.payload.response.BankResponse;
import com.nepbrain.technology.payment.payload.response.PaymentGatewayResponse;
import com.nepbrain.technology.payment.payload.response.PaymentResponse;
import com.nepbrain.technology.payment.payload.response.WalletResponse;
import com.nepbrain.technology.payment.payload.response.WalletTransactionResponse;
import com.nepbrain.technology.payment.repository.BankRepository;
import com.nepbrain.technology.payment.repository.PaymentGatewayRepository;
import com.nepbrain.technology.payment.repository.PaymentRepository;
import com.nepbrain.technology.payment.repository.WalletRepository;
import com.nepbrain.technology.payment.repository.WalletTransactionRepository;
import com.nepbrain.technology.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final BankRepository bankRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;

    @Override
    public GlobalResponse savePayment(PaymentRequest request) {
        PaymentEntity payment = new PaymentEntity();
        payment.setBookingId(request.getBookingId());
        payment.setCustomerId(request.getCustomerId());
        payment.setAmount(request.getAmount());
        payment.setPaymentType(request.getPaymentType());
        payment.setTxnId(request.getTxnId());
        payment.setPaymentStatus(request.getPaymentStatus());
        payment.setDatetime(LocalDateTime.now().toString());
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.payment.save.success", toPaymentResponse(paymentRepository.save(payment)));
    }

    @Override
    public GlobalResponse paymentList(UUID bookingId) {
        List<PaymentResponse> payments = new ArrayList<>();
        for (PaymentEntity payment : paymentRepository.findByBookingId(bookingId)) {
            payments.add(toPaymentResponse(payment));
        }
        return GlobalResponseBuilder.buildSuccessResponse(payments);
    }

    @Override
    public GlobalResponse paymentGateways() {
        List<PaymentGatewayResponse> gateways = new ArrayList<>();
        for (PaymentGatewayEntity gateway : paymentGatewayRepository.findAll()) {
            String status = gateway.getStatus() == null ? null : gateway.getStatus().name();
            gateways.add(PaymentGatewayResponse.builder()
                    .id(gateway.getId())
                    .title(gateway.getTitle())
                    .type(gateway.getType())
                    .isTest(gateway.getIsTest())
                    .status(status)
                    .build());
        }
        return GlobalResponseBuilder.buildSuccessResponse(gateways);
    }

    @Override
    public GlobalResponse userWalletBalance(UUID userId) {
        WalletEntity wallet = walletRepository.findByUserId(userId).orElse(null);
        if (wallet == null) {
            wallet = new WalletEntity();
            wallet.setUserId(userId);
            wallet.setAmount(BigDecimal.ZERO);
            wallet = walletRepository.save(wallet);
        }
        return GlobalResponseBuilder.buildSuccessResponse(toWalletResponse(wallet));
    }

    @Override
    public GlobalResponse walletHistory(UUID userId) {
        List<WalletTransactionResponse> history = new ArrayList<>();
        for (WalletTransactionEntity txn : walletTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId)) {
            history.add(WalletTransactionResponse.builder()
                    .id(txn.getId())
                    .userId(txn.getUserId())
                    .amount(txn.getAmount())
                    .type(txn.getType())
                    .activityType(txn.getActivityType())
                    .datetime(txn.getDatetime())
                    .build());
        }
        return GlobalResponseBuilder.buildSuccessResponse(history);
    }

    @Override
    @Transactional
    public GlobalResponse walletTopUp(WalletTopUpRequest request) {
        WalletEntity wallet = walletRepository.findByUserId(request.getUserId()).orElse(null);
        if (wallet == null) {
            wallet = new WalletEntity();
            wallet.setUserId(request.getUserId());
            wallet.setAmount(BigDecimal.ZERO);
        }
        wallet.setAmount(wallet.getAmount().add(request.getAmount()));
        wallet = walletRepository.save(wallet);

        WalletTransactionEntity txn = new WalletTransactionEntity();
        txn.setUserId(request.getUserId());
        txn.setWalletId(wallet.getId());
        txn.setAmount(request.getAmount());
        txn.setType("credit");
        txn.setActivityType("top_up");
        txn.setDatetime(LocalDateTime.now().toString());
        walletTransactionRepository.save(txn);
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.payment.topup.success", toWalletResponse(wallet));
    }

    @Override
    @Transactional
    public GlobalResponse withdrawMoney(WithdrawRequest request) {
        WalletEntity wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PAY002.name()));
        if (wallet.getAmount().compareTo(request.getAmount()) < 0) {
            throw new GlobalException(ErrorCodeEnum.PAY002.name(), "Insufficient wallet balance");
        }
        wallet.setAmount(wallet.getAmount().subtract(request.getAmount()));
        walletRepository.save(wallet);

        WalletTransactionEntity txn = new WalletTransactionEntity();
        txn.setUserId(request.getUserId());
        txn.setWalletId(wallet.getId());
        txn.setAmount(request.getAmount());
        txn.setType("debit");
        txn.setActivityType("withdraw");
        txn.setDatetime(LocalDateTime.now().toString());
        walletTransactionRepository.save(txn);
        return GlobalResponseBuilder.buildSuccessResponse("message.payment.withdraw.success");
    }

    @Override
    public GlobalResponse saveBank(BankRequest request) {
        BankEntity bank;
        if (request.getId() != null) {
            bank = bankRepository.findById(request.getId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PAY003.name()));
        } else {
            bank = new BankEntity();
        }
        if (request.getBankName() != null) {
            bank.setBankName(request.getBankName());
        }
        if (request.getBranchName() != null) {
            bank.setBranchName(request.getBranchName());
        }
        if (request.getAccountNumber() != null) {
            bank.setAccountNumber(request.getAccountNumber());
        }
        if (request.getIfscNo() != null) {
            bank.setIfscNo(request.getIfscNo());
        }
        if (request.getAccountHolderName() != null) {
            bank.setAccountHolderName(request.getAccountHolderName());
        }
        if (request.getUserId() != null) {
            bank.setUserId(request.getUserId());
        }
        if (request.getIsDefault() != null) {
            bank.setIsDefault(request.getIsDefault());
        }
        if (bank.getStatus() == null) {
            bank.setStatus(StatusEnum.ACTIVE);
        }
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.payment.bank.saved.success", toBankResponse(bankRepository.save(bank)));
    }

    @Override
    public GlobalResponse userBankDetail(UUID userId) {
        List<BankResponse> banks = new ArrayList<>();
        for (BankEntity bank : bankRepository.findByUserId(userId)) {
            if (bank.getStatus() == StatusEnum.INACTIVE) {
                continue;
            }
            banks.add(toBankResponse(bank));
        }
        return GlobalResponseBuilder.buildSuccessResponse(banks);
    }

    @Override
    @Transactional
    public GlobalResponse defaultBank(UUID bankId) {
        BankEntity selected = bankRepository.findById(bankId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PAY003.name()));
        if (selected.getStatus() == StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.PAY003.name(), HttpStatus.BAD_REQUEST);
        }
        for (BankEntity bank : bankRepository.findByUserId(selected.getUserId())) {
            bank.setIsDefault(false);
            bankRepository.save(bank);
        }
        selected.setIsDefault(true);
        bankRepository.save(selected);
        return GlobalResponseBuilder.buildSuccessResponse("message.payment.bank.default.success");
    }

    @Override
    public GlobalResponse changeBankStatus(UUID bankId, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.PAY005.name());
        }
        BankEntity bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PAY003.name(), HttpStatus.BAD_REQUEST));
        bank.setStatus(status);
        if (status == StatusEnum.INACTIVE) {
            bank.setIsDefault(false);
        }
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.payment.bank.status.success", toBankResponse(bankRepository.save(bank)));
    }

    @Override
    public GlobalResponse deleteBank(UUID bankId) {
        return changeBankStatus(bankId, StatusEnum.INACTIVE);
    }

    private PaymentResponse toPaymentResponse(PaymentEntity payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType())
                .txnId(payment.getTxnId())
                .paymentStatus(payment.getPaymentStatus())
                .datetime(payment.getDatetime())
                .build();
    }

    private WalletResponse toWalletResponse(WalletEntity wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .amount(wallet.getAmount())
                .build();
    }

    private BankResponse toBankResponse(BankEntity bank) {
        return BankResponse.builder()
                .id(bank.getId())
                .userId(bank.getUserId())
                .bankName(bank.getBankName())
                .branchName(bank.getBranchName())
                .accountNumber(bank.getAccountNumber())
                .ifscNo(bank.getIfscNo())
                .accountHolderName(bank.getAccountHolderName())
                .isDefault(bank.getIsDefault())
                .build();
    }
}
