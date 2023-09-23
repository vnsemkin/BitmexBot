package bitmexbot.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around(value = "@annotation(bitmexbot.aspect.BitmexLog)")
    public void log(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("We are in AOP");
        String name = joinPoint.getSignature().getName();
        String message = joinPoint.getClass().getAnnotation(BitmexLog.class).message();
        log.info("Method : {}, : {}", name, message);
        joinPoint.proceed();
    }
}


