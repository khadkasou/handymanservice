package com.nepbrain.technology.payment.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.payment.entity.PaymentGatewayEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentGatewayRepository extends BaseRepository<PaymentGatewayEntity, UUID> {}
