package com.peknight.common.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author PeKnight
 *
 * Created by PeKnight on 2017/7/29.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCommonConfiguration
@SpringBootApplication
public @interface PekApplication {
}
