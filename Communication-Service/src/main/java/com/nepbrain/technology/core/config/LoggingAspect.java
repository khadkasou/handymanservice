package com.nepbrain.technology.core.config;

import com.nimbusds.jose.shaded.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);


    @Before("execution(* com.nepbrain.technology.hrmsuite.employee.service.*.*(..)) && args(exchange,..)")
    public void logBeforeMethod(JoinPoint joinPoint, ServerWebExchange exchange) {
        log.info("Method Called: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(value = "execution(* com.nepbrain.technology.hrmsuite.employee.service.*.*(..))",
            returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        log.info("Method Executed: {}", joinPoint.getSignature().getName());
        log.info("Result: {}", new Gson().toJson(result));
    }
}
