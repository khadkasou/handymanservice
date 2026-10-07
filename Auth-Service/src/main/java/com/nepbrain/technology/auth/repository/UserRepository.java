package com.nepbrain.technology.auth.repository;

import com.nepbrain.technology.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity findByEmailAddressOrMobileNumber(String emailAddress, String mobileNumber);

    UserEntity findByEmailAddressIgnoreCaseOrMobileNumber(String emailAddress, String mobileNumber);

    UserEntity findByEmailAddress(String emailAddress);

    UserEntity findByEmailAddressIgnoreCase(String emailAddress);

    /** Avoids LazyInitializationException when roles are accessed outside an open session. */
    @Query(value = """
            SELECT ur.role_id
            FROM user_role ur
            WHERE ur.user_id = :userId
            """, nativeQuery = true)
    List<UUID> findRoleIdsByUserId(@Param("userId") UUID userId);
}
