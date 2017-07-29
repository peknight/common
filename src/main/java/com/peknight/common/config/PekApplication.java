package com.peknight.common.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;

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
@EnableCommonConfiguration
@SpringBootApplication
public @interface PekApplication {
}
