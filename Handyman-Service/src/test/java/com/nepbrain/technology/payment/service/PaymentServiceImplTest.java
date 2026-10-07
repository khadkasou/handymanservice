package com.nepbrain.technology.payment.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.payment.entity.*;
import com.nepbrain.technology.payment.payload.request.*;
import com.nepbrain.technology.payment.payload.response.*;
import com.nepbrain.technology.payment.repository.*;
import com.nepbrain.technology.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private WalletTransactionRepository walletTransactionRepository;
    @Mock private BankRepository bankRepository;
    @Mock private PaymentGatewayRepository paymentGatewayRepository;

    @Spy
    private ModelMapper modelMapper = configuredMapper();

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private UUID userId;
    private UUID bookingId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
    }

    private static ModelMapper configuredMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        return mapper;
    }

    @Test
    void savePayment_success() {
        PaymentRequest request = PaymentRequest.builder()
                .bookingId(bookingId)
                .customerId(userId)
                .amount(new BigDecimal("150.00"))
                .paymentType("cash")
                .txnId("TXN-1")
                .paymentStatus("paid")
                .build();

        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(invocation -> {
            PaymentEntity e = invocation.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        PaymentResponse response = paymentService.savePayment(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getBookingId()).isEqualTo(bookingId);
        assertThat(response.getAmount()).isEqualByComparingTo("150.00");
        assertThat(response.getDatetime()).isNotBlank();
        verify(paymentRepository).save(any(PaymentEntity.class));
    }

    @Test
    void paymentList_success() {
        PaymentEntity entity = PaymentEntity.builder()
                .bookingId(bookingId)
                .customerId(userId)
                .amount(BigDecimal.TEN)
                .paymentType("online")
                .build();
        entity.setId(UUID.randomUUID());
        when(paymentRepository.findByBookingId(bookingId)).thenReturn(List.of(entity));

        List<PaymentResponse> result = paymentService.paymentList(bookingId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getBookingId()).isEqualTo(bookingId);
        verify(paymentRepository).findByBookingId(bookingId);
    }

    @Test
    void paymentGateways_success() {
        PaymentGatewayEntity gateway = PaymentGatewayEntity.builder()
                .title("Stripe")
                .type("stripe")
                .isTest(true)
                .status(StatusEnum.ACTIVE)
                .build();
        gateway.setId(UUID.randomUUID());
        when(paymentGatewayRepository.findAll()).thenReturn(List.of(gateway));

        List<PaymentGatewayResponse> result = paymentService.paymentGateways();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Stripe");
        assertThat(result.getFirst().getStatus()).isEqualTo("ACTIVE");
        verify(paymentGatewayRepository).findAll();
    }

    @Test
    void userWalletBalance_returnsExistingWallet() {
        WalletEntity wallet = WalletEntity.builder().userId(userId).amount(new BigDecimal("50")).build();
        wallet.setId(UUID.randomUUID());
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        WalletResponse response = paymentService.userWalletBalance(userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getAmount()).isEqualByComparingTo("50");
        verify(walletRepository, never()).save(any());
    }

    @Test
    void userWalletBalance_createsWalletWhenMissing() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(walletRepository.save(any(WalletEntity.class))).thenAnswer(invocation -> {
            WalletEntity e = invocation.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        WalletResponse response = paymentService.userWalletBalance(userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(walletRepository).save(any(WalletEntity.class));
    }

    @Test
    void walletHistory_success() {
        WalletTransactionEntity tx = WalletTransactionEntity.builder()
                .userId(userId)
                .amount(BigDecimal.ONE)
                .type("credit")
                .activityType("top_up")
                .build();
        tx.setId(UUID.randomUUID());
        when(walletTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(tx));

        List<WalletTransactionResponse> result = paymentService.walletHistory(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getType()).isEqualTo("credit");
        verify(walletTransactionRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void walletTopUp_existingWallet_success() {
        WalletEntity wallet = WalletEntity.builder().userId(userId).amount(new BigDecimal("20")).build();
        wallet.setId(UUID.randomUUID());
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(WalletEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(walletTransactionRepository.save(any(WalletTransactionEntity.class))).thenAnswer(inv -> {
            WalletTransactionEntity e = inv.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        WalletTopUpRequest request = WalletTopUpRequest.builder()
                .userId(userId)
                .amount(new BigDecimal("30"))
                .build();

        WalletResponse response = paymentService.walletTopUp(request);

        assertThat(response.getAmount()).isEqualByComparingTo("50");
        verify(walletTransactionRepository).save(argThat(t -> "credit".equals(t.getType()) && "top_up".equals(t.getActivityType())));
    }

    @Test
    void walletTopUp_createsWalletWhenMissing() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(walletRepository.save(any(WalletEntity.class))).thenAnswer(invocation -> {
            WalletEntity e = invocation.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });
        when(walletTransactionRepository.save(any(WalletTransactionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        WalletResponse response = paymentService.walletTopUp(
                WalletTopUpRequest.builder().userId(userId).amount(new BigDecimal("10")).build());

        assertThat(response.getAmount()).isEqualByComparingTo("10");
        verify(walletRepository).save(any(WalletEntity.class));
    }

    @Test
    void withdrawMoney_success() {
        WalletEntity wallet = WalletEntity.builder().userId(userId).amount(new BigDecimal("100")).build();
        wallet.setId(UUID.randomUUID());
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(WalletEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(walletTransactionRepository.save(any(WalletTransactionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        paymentService.withdrawMoney(WithdrawRequest.builder().userId(userId).amount(new BigDecimal("40")).build());

        assertThat(wallet.getAmount()).isEqualByComparingTo("60");
        verify(walletTransactionRepository).save(argThat(t -> "debit".equals(t.getType()) && "withdraw".equals(t.getActivityType())));
    }

    @Test
    void withdrawMoney_walletNotFound_throwsGlobalException() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.withdrawMoney(
                WithdrawRequest.builder().userId(userId).amount(BigDecimal.TEN).build()))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PAY002");
    }

    @Test
    void withdrawMoney_insufficientBalance_throwsGlobalException() {
        WalletEntity wallet = WalletEntity.builder().userId(userId).amount(new BigDecimal("5")).build();
        wallet.setId(UUID.randomUUID());
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> paymentService.withdrawMoney(
                WithdrawRequest.builder().userId(userId).amount(new BigDecimal("10")).build()))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("Insufficient wallet balance")
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PAY002");
    }

    @Test
    void saveBank_create_success() {
        BankRequest request = BankRequest.builder()
                .userId(userId)
                .bankName("Nabil")
                .branchName("Kathmandu")
                .accountNumber("123")
                .ifscNo("NABIL")
                .accountHolderName("John")
                .isDefault(true)
                .build();
        when(bankRepository.save(any(BankEntity.class))).thenAnswer(invocation -> {
            BankEntity e = invocation.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        BankResponse response = paymentService.saveBank(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getBankName()).isEqualTo("Nabil");
        assertThat(response.getAccountHolderName()).isEqualTo("John");
        verify(bankRepository).save(argThat(b -> StatusEnum.ACTIVE.equals(b.getStatus())));
    }

    @Test
    void saveBank_update_success() {
        UUID bankId = UUID.randomUUID();
        BankEntity existing = BankEntity.builder()
                .userId(userId)
                .bankName("Old")
                .status(StatusEnum.ACTIVE)
                .build();
        existing.setId(bankId);
        when(bankRepository.findById(bankId)).thenReturn(Optional.of(existing));
        when(bankRepository.save(any(BankEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        BankResponse response = paymentService.saveBank(BankRequest.builder()
                .id(bankId)
                .bankName("New Bank")
                .build());

        assertThat(response.getBankName()).isEqualTo("New Bank");
        verify(bankRepository).findById(bankId);
    }

    @Test
    void saveBank_update_notFound_throwsGlobalException() {
        UUID bankId = UUID.randomUUID();
        when(bankRepository.findById(bankId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.saveBank(BankRequest.builder().id(bankId).bankName("X").build()))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PAY003");
    }

    @Test
    void userBankDetail_success() {
        BankEntity bank = BankEntity.builder().userId(userId).bankName("Nabil").status(StatusEnum.ACTIVE).build();
        bank.setId(UUID.randomUUID());
        when(bankRepository.findByUserId(userId)).thenReturn(List.of(bank));

        List<BankResponse> result = paymentService.userBankDetail(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getBankName()).isEqualTo("Nabil");
        verify(bankRepository).findByUserId(userId);
    }

    @Test
    void defaultBank_success() {
        UUID bankId = UUID.randomUUID();
        BankEntity bank = BankEntity.builder().userId(userId).isDefault(false).status(StatusEnum.ACTIVE).build();
        bank.setId(bankId);
        BankEntity other = BankEntity.builder().userId(userId).isDefault(true).status(StatusEnum.ACTIVE).build();
        other.setId(UUID.randomUUID());

        when(bankRepository.findById(bankId)).thenReturn(Optional.of(bank));
        when(bankRepository.findByUserId(userId)).thenReturn(List.of(bank, other));
        when(bankRepository.save(any(BankEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        paymentService.defaultBank(bankId);

        assertThat(bank.getIsDefault()).isTrue();
        assertThat(other.getIsDefault()).isFalse();
        verify(bankRepository, atLeast(2)).save(any(BankEntity.class));
    }

    @Test
    void defaultBank_notFound_throwsGlobalException() {
        UUID bankId = UUID.randomUUID();
        when(bankRepository.findById(bankId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.defaultBank(bankId))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PAY003");
    }

    @Test
    void deleteBank_success() {
        UUID bankId = UUID.randomUUID();
        when(bankRepository.existsById(bankId)).thenReturn(true);

        paymentService.deleteBank(bankId);

        verify(bankRepository).deleteById(bankId);
    }

    @Test
    void deleteBank_notFound_throwsGlobalException() {
        UUID bankId = UUID.randomUUID();
        when(bankRepository.existsById(bankId)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.deleteBank(bankId))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("PAY003");
        verify(bankRepository, never()).deleteById(any());
    }
}
