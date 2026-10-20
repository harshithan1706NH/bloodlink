package com.bloodlink.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Records controller execution time without coupling controllers to logging. */
@Aspect
@Component
public class ApiTimingAspect {
    private static final Logger log = LoggerFactory.getLogger(ApiTimingAspect.class);

    @Around("within(com.bloodlink.controller..*) && @within(org.springframework.web.bind.annotation.RestController)")
    public Object timeApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000;
            log.info("API {} completed in {} ms", joinPoint.getSignature().toShortString(), elapsedMillis);
        }
    }
}
