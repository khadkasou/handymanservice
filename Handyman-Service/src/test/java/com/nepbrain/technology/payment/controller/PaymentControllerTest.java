package com.nepbrain.technology.payment.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.payment.payload.request.*;
import com.nepbrain.technology.payment.payload.response.*;
import com.nepbrain.technology.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock private PaymentService paymentService;

    @InjectMocks private PaymentController paymentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void savePayment_returnsSuccess() throws Exception {
        UUID bookingId = UUID.randomUUID();
        PaymentRequest request = PaymentRequest.builder()
                .bookingId(bookingId)
                .amount(new BigDecimal("100"))
                .paymentType("cash")
                .build();
        when(paymentService.savePayment(any(PaymentRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(PaymentResponse.builder()
                .id(UUID.randomUUID())
                .bookingId(bookingId)
                .amount(new BigDecimal("100"))
                .build()).build());

        mockMvc.perform(post("/api/v2/payment/save-payment").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).savePayment(any(PaymentRequest.class));
    }

    @Test
    void paymentList_returnsSuccess() throws Exception {
        UUID bookingId = UUID.randomUUID();
        when(paymentService.paymentList(bookingId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(PaymentResponse.builder()
                .id(UUID.randomUUID())
                .bookingId(bookingId)
                .build())).build());

        mockMvc.perform(get("/api/v2/payment/payment-list").param("booking_id", bookingId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).paymentList(bookingId);
    }

    @Test
    void paymentGateways_returnsSuccess() throws Exception {
        when(paymentService.paymentGateways()).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(PaymentGatewayResponse.builder()
                .id(UUID.randomUUID())
                .title("Stripe")
                .status("ACTIVE")
                .build())).build());

        mockMvc.perform(get("/api/v2/payment/payment-gateways")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).paymentGateways();
    }

    @Test
    void userWalletBalance_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        when(paymentService.userWalletBalance(userId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(WalletResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .amount(BigDecimal.TEN)
                .build()).build());

        mockMvc.perform(get("/api/v2/payment/user-wallet-balance").param("user_id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).userWalletBalance(userId);
    }

    @Test
    void walletHistory_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        when(paymentService.walletHistory(userId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(WalletTransactionResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type("credit")
                .build())).build());

        mockMvc.perform(get("/api/v2/payment/wallet-history").param("user_id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).walletHistory(userId);
    }

    @Test
    void walletTopUp_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        WalletTopUpRequest request = WalletTopUpRequest.builder().userId(userId).amount(new BigDecimal("25")).build();
        when(paymentService.walletTopUp(any(WalletTopUpRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(WalletResponse.builder()
                .userId(userId)
                .amount(new BigDecimal("25"))
                .build()).build());

        mockMvc.perform(post("/api/v2/payment/wallet-top-up").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).walletTopUp(any(WalletTopUpRequest.class));
    }

    @Test
    void withdrawMoney_returnsSuccess() throws Exception {
        WithdrawRequest request = WithdrawRequest.builder()
                .userId(UUID.randomUUID())
                .amount(new BigDecimal("10"))
                .build();
        when(paymentService.withdrawMoney(any(WithdrawRequest.class))).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).build());

        mockMvc.perform(post("/api/v2/payment/withdraw-money").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).withdrawMoney(any(WithdrawRequest.class));
    }

    @Test
    void saveBank_returnsSuccess() throws Exception {
        BankRequest request = BankRequest.builder().userId(UUID.randomUUID()).bankName("Nabil").build();
        when(paymentService.saveBank(any(BankRequest.class)))
                .thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(BankResponse.builder().id(UUID.randomUUID()).bankName("Nabil").build()).build());

        mockMvc.perform(post("/api/v2/payment/save-bank").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).saveBank(any(BankRequest.class));
    }

    @Test
    void userBankDetail_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        when(paymentService.userBankDetail(userId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).data(List.of(BankResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .bankName("Nabil")
                .build())).build());

        mockMvc.perform(get("/api/v2/payment/user-bank-detail").param("user_id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).userBankDetail(userId);
    }

    @Test
    void defaultBank_returnsSuccess() throws Exception {
        UUID bankId = UUID.randomUUID();
        when(paymentService.defaultBank(bankId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).build());

        mockMvc.perform(post("/api/v2/payment/default-bank").param("id", bankId.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).defaultBank(bankId);
    }

    @Test
    void deleteBank_returnsSuccess() throws Exception {
        UUID bankId = UUID.randomUUID();
        when(paymentService.deleteBank(bankId)).thenReturn(GlobalResponse.builder().status(ApiStatusEnum.SUCCESS).build());

        mockMvc.perform(post("/api/v2/payment/delete-bank/{bankId}", bankId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()));

        verify(paymentService).deleteBank(eq(bankId));
    }
}
