package com.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeignConfig {

    private static final String SERVICE_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiU0VSVklDRSIsInN1YiI6InBvc3Qtc2VydmljZSIsImlhdCI6MTczMTMxNzIwNCwiZXhwIjoxNzMxNDAzNjA0fQ.8u4PGUxXiNXqp3dWZAGdZof-3j64EynT5RQZj5D_qtE";

    @Bean
    @Primary
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", SERVICE_TOKEN);
    }
}
