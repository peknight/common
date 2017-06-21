package com.peknight.common.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by PeKnight on 2017/6/21.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommonLog {
    String beginMargin() default "";

    String endMargin() default "";

    String exceptionMargin() default "";

    LoggingLevel level();

    enum LoggingLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
