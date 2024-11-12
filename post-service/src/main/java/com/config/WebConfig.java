//package com.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//@Configuration
//public class WebConfig {
//
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("http://localhost:5173"); // Allow your frontend origin
//        config.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, etc.)
//        config.addAllowedHeader("*"); // Allow all headers, including Authorization
//        config.setAllowCredentials(true); // Allow credentials for JWT handling
//        config.addExposedHeader("Authorization"); // Expose Authorization for the client
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config); // Apply this to all endpoints
//
//        return new CorsFilter(source);
//    }
//}
