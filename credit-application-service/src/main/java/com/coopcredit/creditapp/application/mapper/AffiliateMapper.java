package com.coopcredit.creditapp.application.mapper;

import com.coopcredit.creditapp.application.dto.AffiliateResponse;
import com.coopcredit.creditapp.domain.model.Affiliate;
import org.springframework.stereotype.Component;

/**
 * Simple manual mapper for Affiliate to AffiliateResponse.
 */
@Component
public class AffiliateMapper {

    public AffiliateResponse toResponse(Affiliate affiliate) {
        if (affiliate == null) {
            return null;
        }

        AffiliateResponse response = new AffiliateResponse();
        response.setId(affiliate.getId());
        response.setDocument(affiliate.getDocument());
        response.setFullName(affiliate.getFullName());
        response.setSalary(affiliate.getSalary());
        response.setAffiliationDate(affiliate.getAffiliationDate());
        response.setStatus(affiliate.getStatus().name());

        return response;
    }
}
