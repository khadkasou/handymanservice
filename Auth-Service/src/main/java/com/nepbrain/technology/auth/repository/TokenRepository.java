package com.nepbrain.technology.auth.repository;

import com.nepbrain.technology.auth.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, String> {

    Optional<TokenEntity> findByAccessTokenValue(String accessToken);

    Optional<TokenEntity> findByRefreshTokenValue(String refreshToken);

    @Query("select a from TokenEntity a where a.accessTokenValue = :token or a.refreshTokenValue = :token")
    Optional<TokenEntity> findByStateOrAccessTokenValueOrRefreshTokenValue(
            @Param("token") String token);

}