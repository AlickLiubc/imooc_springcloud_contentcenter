package com.itmuch.contentcenter;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class GlobalFeignConfiguration {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // 开启详细日志
    }

}
