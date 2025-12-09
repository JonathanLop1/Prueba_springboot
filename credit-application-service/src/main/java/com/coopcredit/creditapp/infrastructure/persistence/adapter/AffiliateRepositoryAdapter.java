package com.coopcredit.creditapp.infrastructure.persistence.adapter;

import com.coopcredit.creditapp.domain.model.Affiliate;
import com.coopcredit.creditapp.domain.port.AffiliateRepositoryPort;
import com.coopcredit.creditapp.infrastructure.persistence.entity.AffiliateEntity;
import com.coopcredit.creditapp.infrastructure.persistence.mapper.AffiliateEntityMapper;
import com.coopcredit.creditapp.infrastructure.persistence.repository.AffiliateJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing AffiliateRepositoryPort using JPA.
 * This bridges the domain layer with the infrastructure layer.
 */
@Component
public class AffiliateRepositoryAdapter implements AffiliateRepositoryPort {

    private final AffiliateJpaRepository jpaRepository;
    private final AffiliateEntityMapper entityMapper;

    public AffiliateRepositoryAdapter(AffiliateJpaRepository jpaRepository,
            AffiliateEntityMapper entityMapper) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entity = entityMapper.toEntity(affiliate);
        AffiliateEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<Affiliate> findById(Long id) {
        return jpaRepository.findById(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Affiliate> findByDocument(String document) {
        return jpaRepository.findByDocument(document)
                .map(entityMapper::toDomain);
    }

    @Override
    public List<Affiliate> findAll() {
        return jpaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByDocument(String document) {
        return jpaRepository.existsByDocument(document);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }
}
