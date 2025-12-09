package com.coopcredit.creditapp.infrastructure.persistence.adapter;

import com.coopcredit.creditapp.domain.model.User;
import com.coopcredit.creditapp.domain.port.UserRepositoryPort;
import com.coopcredit.creditapp.infrastructure.persistence.entity.UserEntity;
import com.coopcredit.creditapp.infrastructure.persistence.mapper.UserEntityMapper;
import com.coopcredit.creditapp.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing UserRepositoryPort using JPA.
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper entityMapper;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository,
            UserEntityMapper entityMapper) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = entityMapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(entityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(entityMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }
}
