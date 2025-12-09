package com.coopcredit.creditapp.infrastructure.persistence.adapter;

import com.coopcredit.creditapp.domain.model.ApplicationStatus;
import com.coopcredit.creditapp.domain.model.CreditApplication;
import com.coopcredit.creditapp.domain.port.CreditApplicationRepositoryPort;
import com.coopcredit.creditapp.infrastructure.persistence.entity.CreditApplicationEntity;
import com.coopcredit.creditapp.infrastructure.persistence.mapper.CreditApplicationEntityMapper;
import com.coopcredit.creditapp.infrastructure.persistence.repository.CreditApplicationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing CreditApplicationRepositoryPort using JPA.
 */
@Component
public class CreditApplicationRepositoryAdapter implements CreditApplicationRepositoryPort {

    private final CreditApplicationJpaRepository jpaRepository;
    private final CreditApplicationEntityMapper entityMapper;

    public CreditApplicationRepositoryAdapter(CreditApplicationJpaRepository jpaRepository,
            CreditApplicationEntityMapper entityMapper) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public CreditApplication save(CreditApplication application) {
        CreditApplicationEntity entity = entityMapper.toEntity(application);
        CreditApplicationEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<CreditApplication> findById(Long id) {
        return jpaRepository.findById(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public List<CreditApplication> findAll() {
        return jpaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CreditApplication> findByAffiliateId(Long affiliateId) {
        return jpaRepository.findByAffiliateId(affiliateId).stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CreditApplication> findByStatus(ApplicationStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }
}
