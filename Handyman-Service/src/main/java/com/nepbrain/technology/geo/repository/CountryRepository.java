package com.nepbrain.technology.geo.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.geo.entity.CountryEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CountryRepository extends BaseRepository<CountryEntity, UUID> {}
