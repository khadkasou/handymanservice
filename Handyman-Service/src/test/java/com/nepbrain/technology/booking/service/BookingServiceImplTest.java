package com.nepbrain.technology.booking.service;

import com.nepbrain.technology.booking.entity.BookingEntity;
import com.nepbrain.technology.booking.entity.BookingStatusEntity;
import com.nepbrain.technology.booking.entity.CouponEntity;
import com.nepbrain.technology.booking.enums.BookingStatusEnum;
import com.nepbrain.technology.booking.payload.request.BookingRequest;
import com.nepbrain.technology.booking.payload.response.BookingResponse;
import com.nepbrain.technology.booking.payload.response.BookingStatusResponse;
import com.nepbrain.technology.booking.payload.response.CouponResponse;
import com.nepbrain.technology.booking.payload.response.LocationResponse;
import com.nepbrain.technology.booking.repository.BookingRepository;
import com.nepbrain.technology.booking.repository.BookingStatusRepository;
import com.nepbrain.technology.booking.repository.CouponRepository;
import com.nepbrain.technology.booking.service.impl.BookingServiceImpl;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingStatusRepository bookingStatusRepository;
    @Mock
    private CouponRepository couponRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void stubSave() {
        lenient().when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(invocation -> {
            BookingEntity entity = invocation.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            return entity;
        });
    }

    @Test
    void bookingSave_success_defaultsPending() {
        UUID customerId = UUID.randomUUID();
        BookingRequest request = BookingRequest.builder()
                .customerId(customerId)
                .serviceId(UUID.randomUUID())
                .address("Kathmandu")
                .totalAmount(new BigDecimal("1500"))
                .build();

        BookingResponse response = bookingService.bookingSave(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(customerId);
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getAddress()).isEqualTo("Kathmandu");
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void bookingSave_withInvalidStatus_throwsBkg003() {
        BookingRequest request = BookingRequest.builder()
                .customerId(UUID.randomUUID())
                .status("not-a-real-status")
                .build();

        assertThatThrownBy(() -> bookingService.bookingSave(request))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("BKG003");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookingUpdate_success() {
        UUID id = UUID.randomUUID();
        BookingEntity existing = BookingEntity.builder()
                .customerId(UUID.randomUUID())
                .status(BookingStatusEnum.PENDING)
                .address("Old")
                .build();
        existing.setId(id);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(existing));

        BookingResponse response = bookingService.bookingUpdate(BookingRequest.builder()
                .id(id)
                .status("COMPLETED")
                .address("New Address")
                .handymanId(UUID.randomUUID())
                .totalAmount(new BigDecimal("2000"))
                .build());

        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getAddress()).isEqualTo("New Address");
        assertThat(existing.getTotalAmount()).isEqualByComparingTo("2000");
        verify(bookingRepository).save(existing);
    }

    @Test
    void bookingUpdate_whenMissing_throwsBkg001() {
        UUID id = UUID.randomUUID();
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.bookingUpdate(BookingRequest.builder().id(id).build()))
                .isInstanceOf(GlobalException.class)
                .hasMessage("Booking not found")
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("BKG001");
    }

    @Test
    void bookingDetail_success() {
        UUID id = UUID.randomUUID();
        BookingEntity entity = BookingEntity.builder()
                .customerId(UUID.randomUUID())
                .status(BookingStatusEnum.ACCEPT)
                .build();
        entity.setId(id);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(entity));

        BookingResponse response = bookingService.bookingDetail(id);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getStatus()).isEqualTo("ACCEPT");
    }

    @Test
    void bookingDetail_whenMissing_throwsBkg001() {
        UUID id = UUID.randomUUID();
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.bookingDetail(id))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("BKG001");
    }

    @Test
    void bookingList_filtersByCustomer() {
        UUID customerId = UUID.randomUUID();
        BookingEntity entity = BookingEntity.builder()
                .customerId(customerId)
                .status(BookingStatusEnum.PENDING)
                .build();
        entity.setId(UUID.randomUUID());
        when(bookingRepository.findByCustomerId(customerId)).thenReturn(List.of(entity));

        List<BookingResponse> list = bookingService.bookingList(customerId);

        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getCustomerId()).isEqualTo(customerId);
        verify(bookingRepository).findByCustomerId(customerId);
        verify(bookingRepository, never()).findAll();
    }

    @Test
    void bookingList_whenCustomerNull_returnsAll() {
        BookingEntity entity = BookingEntity.builder().status(BookingStatusEnum.PENDING).build();
        entity.setId(UUID.randomUUID());
        when(bookingRepository.findAll()).thenReturn(List.of(entity));

        assertThat(bookingService.bookingList(null)).hasSize(1);
        verify(bookingRepository).findAll();
    }

    @Test
    void bookingStatus_mapsEntities() {
        BookingStatusEntity status = BookingStatusEntity.builder()
                .value("pending")
                .label("Pending")
                .color("#fff")
                .sortOrder(1)
                .build();
        status.setId(UUID.randomUUID());
        when(bookingStatusRepository.findAll()).thenReturn(List.of(status));

        List<BookingStatusResponse> responses = bookingService.bookingStatus();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getValue()).isEqualTo("pending");
        assertThat(responses.getFirst().getLabel()).isEqualTo("Pending");
    }

    @Test
    void getLocation_success() {
        UUID id = UUID.randomUUID();
        BookingEntity entity = BookingEntity.builder()
                .latitude(27.7)
                .longitude(85.3)
                .address("Patan")
                .status(BookingStatusEnum.ON_GOING)
                .build();
        entity.setId(id);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(entity));

        LocationResponse response = bookingService.getLocation(id);

        assertThat(response.getBookingId()).isEqualTo(id);
        assertThat(response.getLatitude()).isEqualTo(27.7);
        assertThat(response.getLongitude()).isEqualTo(85.3);
        assertThat(response.getAddress()).isEqualTo("Patan");
    }

    @Test
    void getLocation_whenMissing_throwsBkg001() {
        UUID id = UUID.randomUUID();
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getLocation(id))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("BKG001");
    }

    @Test
    void downloadInvoice_success() {
        UUID id = UUID.randomUUID();
        BookingEntity entity = BookingEntity.builder().status(BookingStatusEnum.COMPLETED).build();
        entity.setId(id);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(entity));

        String invoice = bookingService.downloadInvoice(id);

        assertThat(invoice).contains(id.toString());
    }

    @Test
    void downloadInvoice_whenMissing_throwsBkg001() {
        UUID id = UUID.randomUUID();
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.downloadInvoice(id))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getCode())
                .isEqualTo("BKG001");
    }

    @Test
    void couponList_mapsStatus() {
        UUID serviceId = UUID.randomUUID();
        CouponEntity coupon = CouponEntity.builder()
                .code("SAVE10")
                .serviceId(serviceId)
                .status(StatusEnum.ACTIVE)
                .build();
        coupon.setId(UUID.randomUUID());
        when(couponRepository.findByServiceId(serviceId)).thenReturn(List.of(coupon));

        List<CouponResponse> coupons = bookingService.couponList(serviceId);

        assertThat(coupons).hasSize(1);
        assertThat(coupons.getFirst().getCode()).isEqualTo("SAVE10");
        assertThat(coupons.getFirst().getStatus()).isEqualTo("ACTIVE");
        verify(couponRepository).findByServiceId(serviceId);
    }
}
