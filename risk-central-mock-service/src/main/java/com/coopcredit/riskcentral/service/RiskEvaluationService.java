package com.coopcredit.riskcentral.service;

import com.coopcredit.riskcentral.dto.RiskEvaluationRequest;
import com.coopcredit.riskcentral.dto.RiskEvaluationResponse;
import org.springframework.stereotype.Service;

/**
 * Service implementing consistent risk evaluation logic based on document hash.
 * The same document will always return the same score.
 */
@Service
public class RiskEvaluationService {

    private static final int MIN_SCORE = 300;
    private static final int MAX_SCORE = 950;
    private static final int RANGE = MAX_SCORE - MIN_SCORE;

    public RiskEvaluationResponse evaluateRisk(RiskEvaluationRequest request) {
        // Generate consistent score based on document hash
        int score = calculateScore(request.getDocumento());

        // Determine risk level
        String riskLevel = determineRiskLevel(score);

        // Generate detail message
        String detail = generateDetail(riskLevel, score);

        return new RiskEvaluationResponse(
                request.getDocumento(),
                score,
                riskLevel,
                detail);
    }

    /**
     * Calculate a consistent score for a given document.
     * Uses the document's hash code to ensure consistency.
     */
    private int calculateScore(String document) {
        if (document == null || document.isEmpty()) {
            return 500; // Default medium score
        }

        // Use hash to generate a seed
        int hash = document.hashCode();
        int seed = Math.abs(hash % 1000);

        // Map seed (0-999) to score range (300-950)
        int score = MIN_SCORE + (seed * RANGE / 1000);

        // Ensure within bounds
        return Math.min(MAX_SCORE, Math.max(MIN_SCORE, score));
    }

    /**
     * Determine risk level based on score.
     * 300-500: ALTO
     * 501-700: MEDIO
     * 701-950: BAJO
     */
    private String determineRiskLevel(int score) {
        if (score <= 500) {
            return "ALTO";
        } else if (score <= 700) {
            return "MEDIO";
        } else {
            return "BAJO";
        }
    }

    /**
     * Generate a detail message based on risk level.
     */
    private String generateDetail(String riskLevel, int score) {
        return switch (riskLevel) {
            case "BAJO" -> String.format("Buen historial crediticio. Score: %d. Riesgo bajo de incumplimiento.", score);
            case "MEDIO" ->
                String.format("Historial crediticio moderado. Score: %d. Riesgo medio de incumplimiento.", score);
            case "ALTO" ->
                String.format("Historial crediticio deficiente. Score: %d. Riesgo alto de incumplimiento.", score);
            default -> "Evaluación realizada correctamente.";
        };
    }
}
