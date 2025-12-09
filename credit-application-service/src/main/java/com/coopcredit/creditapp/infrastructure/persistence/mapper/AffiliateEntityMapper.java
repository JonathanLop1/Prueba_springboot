package com.coopcredit.creditapp.infrastructure.persistence.mapper;

import com.coopcredit.creditapp.domain.model.Affiliate;
import com.coopcredit.creditapp.infrastructure.persistence.entity.AffiliateEntity;
import org.springframework.stereotype.Component;

/**
 * Simple manual mapper for AffiliateEntity.
 */
@Component
public class AffiliateEntityMapper {

    public Affiliate toDomain(AffiliateEntity entity) {
        if (entity == null) {
            return null;
        }

        Affiliate affiliate = new Affiliate(
                entity.getDocument(),
                entity.getFullName(),
                entity.getSalary(),
                entity.getAffiliationDate());
        affiliate.setId(entity.getId());
        affiliate.setStatus(entity.getStatus());

        return affiliate;
    }

    public AffiliateEntity toEntity(Affiliate affiliate) {
        if (affiliate == null) {
            return null;
        }

        AffiliateEntity entity = new AffiliateEntity();
        entity.setId(affiliate.getId());
        entity.setDocument(affiliate.getDocument());
        entity.setFullName(affiliate.getFullName());
        entity.setSalary(affiliate.getSalary());
        entity.setAffiliationDate(affiliate.getAffiliationDate());
        entity.setStatus(affiliate.getStatus());

        return entity;
    }
}
