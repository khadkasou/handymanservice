package com.nepbrain.technology.payment.service.impl;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.payment.entity.*;
import com.nepbrain.technology.payment.payload.request.*;
import com.nepbrain.technology.payment.payload.response.*;
import com.nepbrain.technology.payment.repository.*;
import com.nepbrain.technology.payment.service.PaymentService;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List; import java.util.UUID;
import java.util.stream.Collectors;
import static com.nepbrain.technology.payment.enums.ErrorCodeEnum.*;

@Service @RequiredArgsConstructor @Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final BankRepository bankRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final ModelMapper modelMapper;

    @Override public PaymentResponse savePayment(PaymentRequest request) {
        PaymentEntity e = modelMapper.map(request, PaymentEntity.class);
        e.setDatetime(LocalDateTime.now().toString());
        return modelMapper.map(paymentRepository.save(e), PaymentResponse.class);
    }
    @Override @Transactional(readOnly = true)
    public List<PaymentResponse> paymentList(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId).stream().map(p -> modelMapper.map(p, PaymentResponse.class)).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public List<PaymentGatewayResponse> paymentGateways() {
        return paymentGatewayRepository.findAll().stream().map(g -> {
            PaymentGatewayResponse r = modelMapper.map(g, PaymentGatewayResponse.class);
            r.setStatus(g.getStatus() != null ? g.getStatus().name() : null); return r;
        }).collect(Collectors.toList());
    }
    @Override public WalletResponse userWalletBalance(UUID userId) {
        WalletEntity w = walletRepository.findByUserId(userId).orElseGet(() ->
                walletRepository.save(WalletEntity.builder().userId(userId).amount(BigDecimal.ZERO).build()));
        return modelMapper.map(w, WalletResponse.class);
    }
    @Override @Transactional(readOnly = true)
    public List<WalletTransactionResponse> walletHistory(UUID userId) {
        return walletTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(t -> modelMapper.map(t, WalletTransactionResponse.class)).collect(Collectors.toList());
    }
    @Override public WalletResponse walletTopUp(WalletTopUpRequest request) {
        WalletEntity w = walletRepository.findByUserId(request.getUserId()).orElseGet(() ->
                WalletEntity.builder().userId(request.getUserId()).amount(BigDecimal.ZERO).build());
        w.setAmount(w.getAmount().add(request.getAmount()));
        w = walletRepository.save(w);
        walletTransactionRepository.save(WalletTransactionEntity.builder()
                .userId(request.getUserId()).walletId(w.getId()).amount(request.getAmount())
                .type("credit").activityType("top_up").datetime(LocalDateTime.now().toString()).build());
        return modelMapper.map(w, WalletResponse.class);
    }
    @Override public void withdrawMoney(WithdrawRequest request) {
        WalletEntity w = walletRepository.findByUserId(request.getUserId()).orElseThrow(() -> new GlobalException(PAY002.name()));
        if (w.getAmount().compareTo(request.getAmount()) < 0) throw new GlobalException(PAY002.name(), "Insufficient wallet balance");
        w.setAmount(w.getAmount().subtract(request.getAmount()));
        walletRepository.save(w);
        walletTransactionRepository.save(WalletTransactionEntity.builder()
                .userId(request.getUserId()).walletId(w.getId()).amount(request.getAmount())
                .type("debit").activityType("withdraw").datetime(LocalDateTime.now().toString()).build());
    }
    @Override public BankResponse saveBank(BankRequest request) {
        BankEntity e = request.getId() != null
                ? bankRepository.findById(request.getId()).orElseThrow(() -> new GlobalException(PAY003.name()))
                : BankEntity.builder().build();
        if (request.getBankName() != null) e.setBankName(request.getBankName());
        if (request.getBranchName() != null) e.setBranchName(request.getBranchName());
        if (request.getAccountNumber() != null) e.setAccountNumber(request.getAccountNumber());
        if (request.getIfscNo() != null) e.setIfscNo(request.getIfscNo());
        if (request.getAccountHolderName() != null) e.setAccountHolderName(request.getAccountHolderName());
        if (request.getUserId() != null) e.setUserId(request.getUserId());
        if (request.getIsDefault() != null) e.setIsDefault(request.getIsDefault());
        if (e.getStatus() == null) e.setStatus(StatusEnum.ACTIVE);
        return modelMapper.map(bankRepository.save(e), BankResponse.class);
    }
    @Override @Transactional(readOnly = true)
    public List<BankResponse> userBankDetail(UUID userId) {
        return bankRepository.findByUserId(userId).stream().map(b -> modelMapper.map(b, BankResponse.class)).collect(Collectors.toList());
    }
    @Override public void defaultBank(UUID bankId) {
        BankEntity bank = bankRepository.findById(bankId).orElseThrow(() -> new GlobalException(PAY003.name()));
        bankRepository.findByUserId(bank.getUserId()).forEach(b -> { b.setIsDefault(false); bankRepository.save(b); });
        bank.setIsDefault(true); bankRepository.save(bank);
    }
    @Override public void deleteBank(UUID bankId) {
        if (!bankRepository.existsById(bankId)) throw new GlobalException(PAY003.name());
        bankRepository.deleteById(bankId);
    }
}
