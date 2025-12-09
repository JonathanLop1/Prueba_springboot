package com.coopcredit.creditapp.domain.port;

import com.coopcredit.creditapp.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for User persistence operations.
 * This is implemented by infrastructure adapters.
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void delete(Long id);
}
