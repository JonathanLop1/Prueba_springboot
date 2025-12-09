package com.coopcredit.creditapp.infrastructure.persistence.mapper;

import com.coopcredit.creditapp.domain.model.User;
import com.coopcredit.creditapp.domain.model.UserRole;
import com.coopcredit.creditapp.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Simple manual mapper for UserEntity.
 */
@Component
public class UserEntityMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        User user = new User(
                entity.getUsername(),
                entity.getPassword(),
                entity.getEmail());
        user.setId(entity.getId());
        user.setRoles(new HashSet<>(entity.getRoles()));
        user.setEnabled(entity.isEnabled());

        return user;
    }

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setEmail(user.getEmail());
        entity.setRoles(new HashSet<>(user.getRoles()));
        entity.setEnabled(user.isEnabled());

        return entity;
    }
}
