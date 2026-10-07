package com.nepbrain.technology.review.service.impl;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.review.entity.BookingRatingEntity;
import com.nepbrain.technology.review.payload.request.BookingRatingRequest;
import com.nepbrain.technology.review.payload.response.BookingRatingResponse;
import com.nepbrain.technology.review.repository.BookingRatingRepository;
import com.nepbrain.technology.review.service.ReviewService;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID; import java.util.stream.Collectors;
import static com.nepbrain.technology.review.enums.ErrorCodeEnum.REV001;

@Service @RequiredArgsConstructor @Transactional
public class ReviewServiceImpl implements ReviewService {
    private final BookingRatingRepository bookingRatingRepository; private final ModelMapper modelMapper;
    @Override public BookingRatingResponse saveBookingRating(BookingRatingRequest request) {
        return modelMapper.map(bookingRatingRepository.save(modelMapper.map(request, BookingRatingEntity.class)), BookingRatingResponse.class);
    }
    @Override @Transactional(readOnly = true)
    public List<BookingRatingResponse> serviceReviews(UUID serviceId) {
        return bookingRatingRepository.findByServiceId(serviceId).stream().map(r -> modelMapper.map(r, BookingRatingResponse.class)).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public List<BookingRatingResponse> getUserRatings(UUID customerId) {
        return bookingRatingRepository.findByCustomerId(customerId).stream().map(r -> modelMapper.map(r, BookingRatingResponse.class)).collect(Collectors.toList());
    }
    @Override public void deleteBookingRating(UUID id) {
        if (!bookingRatingRepository.existsById(id)) throw new GlobalException(REV001.name());
        bookingRatingRepository.deleteById(id);
    }
}
