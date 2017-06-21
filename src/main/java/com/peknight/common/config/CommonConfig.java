package com.peknight.common.config;

import com.peknight.common.logging.CommonLogAspect;
import com.peknight.common.springframework.context.ApplicationContextHolder;
import org.springframework.context.annotation.Bean;

/**
 * Common Configuration
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/21.
 */
public class CommonConfig {
    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public CommonLogAspect commonLogAspect() {
        return new CommonLogAspect();
    }
}
