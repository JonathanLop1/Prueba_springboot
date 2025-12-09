package com.coopcredit.creditapp.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for custom application metrics.
 */
@Configuration
public class MetricsConfig {

    @Bean
    public Counter creditApplicationsCreatedCounter(MeterRegistry registry) {
        return Counter.builder("credit_applications_created_total")
                .description("Total number of credit applications created")
                .tag("type", "creation")
                .register(registry);
    }

    @Bean
    public Counter creditApplicationsEvaluatedCounter(MeterRegistry registry) {
        return Counter.builder("credit_applications_evaluated_total")
                .description("Total number of credit applications evaluated")
                .tag("type", "evaluation")
                .register(registry);
    }

    @Bean
    public Counter authenticationFailuresCounter(MeterRegistry registry) {
        return Counter.builder("authentication_failures_total")
                .description("Total number of failed authentication attempts")
                .tag("type", "security")
                .register(registry);
    }

    @Bean
    public Timer httpRequestTimer(MeterRegistry registry) {
        return Timer.builder("http_request_duration_seconds")
                .description("HTTP request duration in seconds")
                .tag("type", "http")
                .register(registry);
    }
}
