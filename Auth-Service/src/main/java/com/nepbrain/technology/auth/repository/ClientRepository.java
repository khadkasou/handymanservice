package com.nepbrain.technology.auth.repository;

import com.nepbrain.technology.auth.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, String> {

    Optional<ClientEntity> findByClientId(String clientId);
}