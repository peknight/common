package com.peknight.common.annotation;

import com.peknight.common.CommonApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用Common的配置信息
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/21.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CommonApplication.class})
public @interface EnableCommonConfiguration {
}
