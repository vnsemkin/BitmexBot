package bitmex.bitmexspring.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BitmexLog {
    String message() default "";
}
//        IndexController indexController = context.getBean(IndexController.class);
