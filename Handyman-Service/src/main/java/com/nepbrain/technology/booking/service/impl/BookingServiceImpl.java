package com.nepbrain.technology.booking.service.impl;
import com.nepbrain.technology.booking.entity.*;
import com.nepbrain.technology.booking.enums.BookingStatusEnum;
import com.nepbrain.technology.booking.payload.request.BookingRequest;
import com.nepbrain.technology.booking.payload.response.*;
import com.nepbrain.technology.booking.repository.*;
import com.nepbrain.technology.booking.service.BookingService;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID; import java.util.stream.Collectors;
import static com.nepbrain.technology.booking.enums.ErrorCodeEnum.*;

@Service @RequiredArgsConstructor @Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;

    @Override public BookingResponse bookingSave(BookingRequest request) {
        BookingEntity e = modelMapper.map(request, BookingEntity.class);
        e.setStatus(parseStatus(request.getStatus(), BookingStatusEnum.PENDING));
        return toRes(bookingRepository.save(e));
    }
    @Override public BookingResponse bookingUpdate(BookingRequest request) {
        BookingEntity e = bookingRepository.findById(request.getId()).orElseThrow(() -> new GlobalException(BKG001.name()));
        if (request.getStatus() != null) e.setStatus(parseStatus(request.getStatus(), e.getStatus()));
        if (request.getHandymanId() != null) e.setHandymanId(request.getHandymanId());
        if (request.getProviderId() != null) e.setProviderId(request.getProviderId());
        if (request.getStartAt() != null) e.setStartAt(request.getStartAt());
        if (request.getEndAt() != null) e.setEndAt(request.getEndAt());
        if (request.getAddress() != null) e.setAddress(request.getAddress());
        if (request.getLatitude() != null) e.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) e.setLongitude(request.getLongitude());
        if (request.getDescription() != null) e.setDescription(request.getDescription());
        if (request.getTotalAmount() != null) e.setTotalAmount(request.getTotalAmount());
        return toRes(bookingRepository.save(e));
    }
    @Override @Transactional(readOnly = true)
    public BookingResponse bookingDetail(UUID id) {
        return toRes(bookingRepository.findById(id).orElseThrow(() -> new GlobalException(BKG001.name())));
    }
    @Override @Transactional(readOnly = true)
    public List<BookingResponse> bookingList(UUID customerId) {
        List<BookingEntity> list = customerId != null ? bookingRepository.findByCustomerId(customerId) : bookingRepository.findAll();
        return list.stream().map(this::toRes).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public List<BookingStatusResponse> bookingStatus() {
        return bookingStatusRepository.findAll().stream().map(s -> modelMapper.map(s, BookingStatusResponse.class)).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public LocationResponse getLocation(UUID bookingId) {
        BookingEntity e = bookingRepository.findById(bookingId).orElseThrow(() -> new GlobalException(BKG001.name()));
        return LocationResponse.builder().bookingId(e.getId()).latitude(e.getLatitude()).longitude(e.getLongitude()).address(e.getAddress()).build();
    }
    @Override @Transactional(readOnly = true)
    public String downloadInvoice(UUID bookingId) {
        bookingRepository.findById(bookingId).orElseThrow(() -> new GlobalException(BKG001.name()));
        return "Invoice stub for booking " + bookingId;
    }
    @Override @Transactional(readOnly = true)
    public List<CouponResponse> couponList(UUID serviceId) {
        return couponRepository.findByServiceId(serviceId).stream().map(c -> {
            CouponResponse r = modelMapper.map(c, CouponResponse.class);
            r.setStatus(c.getStatus() != null ? c.getStatus().name() : null); return r;
        }).collect(Collectors.toList());
    }
    private BookingStatusEnum parseStatus(String s, BookingStatusEnum def) {
        if (s == null) return def;
        try { return BookingStatusEnum.valueOf(s.trim().toUpperCase().replace("-", "_").replace(" ", "_")); }
        catch (Exception ex) { throw new GlobalException(BKG003.name()); }
    }
    private BookingResponse toRes(BookingEntity e) {
        BookingResponse r = modelMapper.map(e, BookingResponse.class);
        r.setStatus(e.getStatus() != null ? e.getStatus().name() : null); return r;
    }
}
