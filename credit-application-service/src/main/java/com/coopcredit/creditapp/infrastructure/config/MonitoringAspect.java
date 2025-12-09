package com.coopcredit.creditapp.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP aspect for monitoring application performance and operations.
 */
@Aspect
@Component
public class MonitoringAspect {

    private static final Logger log = LoggerFactory.getLogger(MonitoringAspect.class);

    private final Counter createdSuccessCounter;
    private final Counter createdFailureCounter;
    private final Counter evaluatedSuccessCounter;
    private final Counter evaluatedFailureCounter;
    private final Timer creationTimer;
    private final Timer evaluationTimer;

    public MonitoringAspect(MeterRegistry meterRegistry) {
        // Register counters once at construction
        this.createdSuccessCounter = Counter.builder("credit_applications_created_total")
                .tag("type", "creation")
                .tag("status", "success")
                .description("Total credit applications created successfully")
                .register(meterRegistry);

        this.createdFailureCounter = Counter.builder("credit_applications_created_total")
                .tag("type", "creation")
                .tag("status", "failure")
                .description("Total credit applications creation failures")
                .register(meterRegistry);

        this.evaluatedSuccessCounter = Counter.builder("credit_applications_evaluated_total")
                .tag("type", "evaluation")
                .tag("status", "success")
                .description("Total credit applications evaluated successfully")
                .register(meterRegistry);

        this.evaluatedFailureCounter = Counter.builder("credit_applications_evaluated_total")
                .tag("type", "evaluation")
                .tag("status", "failure")
                .description("Total credit applications evaluation failures")
                .register(meterRegistry);

        this.creationTimer = Timer.builder("credit_application_creation_duration")
                .description("Time to create a credit application")
                .register(meterRegistry);

        this.evaluationTimer = Timer.builder("credit_application_evaluation_duration")
                .description("Time to evaluate a credit application")
                .register(meterRegistry);
    }

    @Around("execution(* com.coopcredit.creditapp.application.usecase.RegisterCreditApplicationUseCase.execute(..))")
    public Object monitorCreditApplicationCreation(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start();

        try {
            Object result = joinPoint.proceed();
            createdSuccessCounter.increment();
            log.debug("Credit application created - counter incremented");
            return result;
        } catch (Throwable e) {
            createdFailureCounter.increment();
            log.warn("Credit application creation failed - counter incremented");
            throw e;
        } finally {
            sample.stop(creationTimer);
        }
    }

    @Around("execution(* com.coopcredit.creditapp.application.usecase.EvaluateCreditApplicationUseCase.execute(..))")
    public Object monitorCreditApplicationEvaluation(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start();

        try {
            Object result = joinPoint.proceed();
            evaluatedSuccessCounter.increment();
            log.debug("Credit application evaluated - counter incremented");
            return result;
        } catch (Throwable e) {
            evaluatedFailureCounter.increment();
            log.warn("Credit application evaluation failed - counter incremented");
            throw e;
        } finally {
            sample.stop(evaluationTimer);
        }
    }
}
