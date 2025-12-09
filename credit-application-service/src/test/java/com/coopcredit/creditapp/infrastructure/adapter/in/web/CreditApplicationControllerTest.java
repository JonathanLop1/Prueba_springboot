package com.coopcredit.creditapp.infrastructure.adapter.in.web;

import com.coopcredit.creditapp.application.dto.CreateCreditApplicationRequest;
import com.coopcredit.creditapp.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapp.application.usecase.EvaluateCreditApplicationUseCase;
import com.coopcredit.creditapp.application.usecase.RegisterCreditApplicationUseCase;
import com.coopcredit.creditapp.domain.model.ApplicationStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CreditApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterCreditApplicationUseCase registerUseCase;

    @MockBean
    private EvaluateCreditApplicationUseCase evaluateUseCase;

    @Test
    @WithMockUser(roles = "AFILIADO")
    void shouldCreateApplicationSuccessfully() throws Exception {
        // Arrange
        CreateCreditApplicationRequest request = new CreateCreditApplicationRequest();
        request.setAffiliateId(1L);
        request.setRequestedAmount(new BigDecimal("5000000"));
        request.setTermMonths(12);
        request.setProposedRate(new BigDecimal("1.2"));

        CreditApplicationResponse response = new CreditApplicationResponse();
        response.setId(1L);
        response.setStatus(ApplicationStatus.PENDING.name());
        response.setRequestedAmount(new BigDecimal("5000000"));

        when(registerUseCase.execute(any(CreateCreditApplicationRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ANALISTA")
    void shouldEvaluateApplicationSuccessfully() throws Exception {
        // Arrange
        CreditApplicationResponse response = new CreditApplicationResponse();
        response.setId(1L);
        response.setStatus(ApplicationStatus.APPROVED.name());

        when(evaluateUseCase.execute(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/applications/1/evaluate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @WithMockUser(roles = "AFILIADO") // Wrong role
    void shouldDenyEvaluationAccessForAffiliate() throws Exception {
        mockMvc.perform(post("/api/applications/1/evaluate"))
                .andExpect(status().isForbidden());
    }
}
