package com.nepbrain.technology.provider.service.impl;
import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import com.nepbrain.technology.auth.payload.response.UserResponse;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.provider.entity.*;
import com.nepbrain.technology.provider.payload.request.*;
import com.nepbrain.technology.provider.payload.response.HandymanRatingResponse;
import com.nepbrain.technology.provider.repository.*;
import com.nepbrain.technology.provider.service.ProviderService;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID; import java.util.stream.Collectors;
import static com.nepbrain.technology.provider.enums.ErrorCodeEnum.*;

@Service @RequiredArgsConstructor @Transactional
public class ProviderServiceImpl implements ProviderService {
    private final UserRepository userRepository;
    private final FavouriteProviderRepository favouriteProviderRepository;
    private final HandymanRatingRepository handymanRatingRepository;
    private final ModelMapper modelMapper;

    @Override @Transactional(readOnly = true)
    public List<UserResponse> userList(String userType) {
        UserTypeEnum type = UserTypeEnum.valueOf(userType.trim().toUpperCase());
        return userRepository.findByUserType(type).stream().map(this::toUser).collect(Collectors.toList());
    }
    @Override @Transactional(readOnly = true)
    public UserResponse providerDetail(UUID id) {
        return toUser(userRepository.findById(id).orElseThrow(() -> new GlobalException(PRV001.name())));
    }
    @Override public void saveFavouriteProvider(FavouriteProviderRequest request) {
        favouriteProviderRepository.findByUserIdAndProviderId(request.getUserId(), request.getProviderId())
                .orElseGet(() -> favouriteProviderRepository.save(FavouriteProviderEntity.builder()
                        .userId(request.getUserId()).providerId(request.getProviderId()).build()));
    }
    @Override public void deleteFavouriteProvider(FavouriteProviderRequest request) {
        favouriteProviderRepository.deleteByUserIdAndProviderId(request.getUserId(), request.getProviderId());
    }
    @Override @Transactional(readOnly = true)
    public List<UserResponse> userFavouriteProvider(UUID userId) {
        return favouriteProviderRepository.findByUserId(userId).stream()
                .map(f -> userRepository.findById(f.getProviderId()).orElse(null))
                .filter(u -> u != null).map(this::toUser).collect(Collectors.toList());
    }
    @Override public HandymanRatingResponse saveHandymanRating(HandymanRatingRequest request) {
        return modelMapper.map(handymanRatingRepository.save(modelMapper.map(request, HandymanRatingEntity.class)), HandymanRatingResponse.class);
    }
    @Override @Transactional(readOnly = true)
    public List<HandymanRatingResponse> handymanReviews(UUID handymanId) {
        return handymanRatingRepository.findByHandymanId(handymanId).stream()
                .map(r -> modelMapper.map(r, HandymanRatingResponse.class)).collect(Collectors.toList());
    }
    @Override public void deleteHandymanRating(UUID id) {
        if (!handymanRatingRepository.existsById(id)) throw new GlobalException(PRV003.name());
        handymanRatingRepository.deleteById(id);
    }
    private UserResponse toUser(UserEntity user) {
        UserResponse r = modelMapper.map(user, UserResponse.class);
        r.setUserType(user.getUserType() != null ? user.getUserType().name().toLowerCase() : null);
        r.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        return r;
    }
}
