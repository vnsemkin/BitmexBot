package bitmex.bitmexspring.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(BitmexLog)")
    public void log(ProceedingJoinPoint joinPoint) throws Throwable {
        String name = joinPoint.getSignature().getName();
        String message = joinPoint.getClass().getAnnotation(BitmexLog.class).message();
        log.info("Method : {}, : {}", name, message);
        joinPoint.proceed();
    }
}
