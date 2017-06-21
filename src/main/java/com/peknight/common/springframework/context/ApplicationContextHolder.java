package com.peknight.common.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/21.
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static void contextLoads(Class<?> clazz, String[] args) {
        SpringApplication application = new SpringApplication(clazz);
        applicationContext = application.run(args);
    }

    public static void contextLoads(Class<?> clazz, String[] args, Banner.Mode mode) {
        SpringApplication application = new SpringApplication(clazz);
        application.setBannerMode(mode);
        applicationContext = application.run(args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (ApplicationContextHolder.applicationContext == null) {
            ApplicationContextHolder.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}

