/*
 * (c) Copyright 2025 Digital Technologies and Platforms LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.digtp.start.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * AOP aspect for logging slow operations.
 *
 * <p>Logs execution time for service methods that exceed performance thresholds:
 * <ul>
 *   <li>Services: >100ms</li>
 *   <li>Views: >500ms</li>
 * </ul>
 *
 * <p>Logs at DEBUG level with method name, duration, and parameter summary
 * for performance monitoring and optimization.
 */
@Aspect
@Component
@Slf4j
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentSize, PMD.AtLeastOneConstructor, PMD.CommentRequired, PMD.GuardLogStatement
// - IllegalThrows: AOP @Around requires Throwable for proper exception propagation
@SuppressWarnings({
    "PMD.CommentSize",
    "PMD.AtLeastOneConstructor",
    "PMD.CommentRequired",
    "PMD.GuardLogStatement",
    "checkstyle:IllegalThrows"
})
public class PerformanceLoggingAspect {

    /**
     * Threshold for slow service operations (milliseconds).
     */
    private static final long SERVICE_THRESHOLD_MS = 100;

    /**
     * Threshold for slow view operations (milliseconds).
     */
    private static final long VIEW_THRESHOLD_MS = 500;

    /**
     * Logs slow service method executions.
     *
     * <p>Intercepts all public methods in service package and logs if execution
     * time exceeds SERVICE_THRESHOLD_MS.
     *
     * @param joinPoint method execution join point
     * @return method return value
     * @throws Throwable if method execution throws exception
     */
    @Around("execution(public * com.digtp.start.service..*(..))")
    public Object logServicePerformance(final ProceedingJoinPoint joinPoint) throws Throwable {
        return logPerformance(joinPoint, SERVICE_THRESHOLD_MS, "service");
    }

    /**
     * Logs slow view method executions.
     *
     * <p>Intercepts all public methods in view package and logs if execution
     * time exceeds VIEW_THRESHOLD_MS.
     *
     * @param joinPoint method execution join point
     * @return method return value
     * @throws Throwable if method execution throws exception
     */
    @Around("execution(public * com.digtp.start.view..*(..))")
    public Object logViewPerformance(final ProceedingJoinPoint joinPoint) throws Throwable {
        return logPerformance(joinPoint, VIEW_THRESHOLD_MS, "view");
    }

    /**
     * Logs performance if execution time exceeds threshold.
     *
     * @param joinPoint method execution join point
     * @param thresholdMs threshold in milliseconds
     * @param layer layer name (service/view) for logging
     * @return method return value
     * @throws Throwable if method execution throws exception
     */
    private Object logPerformance(final ProceedingJoinPoint joinPoint, final long thresholdMs, final String layer)
            throws Throwable {
        final long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            final long duration = System.currentTimeMillis() - startTime;
            if (duration > thresholdMs && log.isDebugEnabled()) {
                final String methodName = getMethodName(joinPoint);
                final String className = getClassName(joinPoint);
                log.debug(
                        "Slow {} operation: class={}, method={}, duration={}ms",
                        layer,
                        className,
                        methodName,
                        duration);
            }
        }
    }

    /**
     * Extracts method name from join point.
     *
     * @param joinPoint method execution join point
     * @return method name with parameter types
     */
    private String getMethodName(final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }

    /**
     * Extracts class name from join point.
     *
     * <p>Handles both instance and static methods. For static methods, getTarget()
     * returns null, so we use the declaring type from the signature instead.
     *
     * @param joinPoint method execution join point
     * @return simple class name
     */
    private String getClassName(final ProceedingJoinPoint joinPoint) {
        final Object target = joinPoint.getTarget();
        if (target != null) {
            return target.getClass().getSimpleName();
        }
        // For static methods, getTarget() returns null, use declaring type from signature
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final String declaringTypeName = signature.getDeclaringTypeName();
        final int lastDot = declaringTypeName.lastIndexOf('.');
        return lastDot >= 0 ? declaringTypeName.substring(lastDot + 1) : declaringTypeName;
    }
}
