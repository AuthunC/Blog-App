package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
	protected SecurityFilterChain customConfig(HttpSecurity http) throws Exception {
		
		return http
				.csrf(customizer->customizer.disable())
				.authorizeHttpRequests(
					req->req
					.requestMatchers("api/posts", "api/posts/{id}", "/notifications/stream", "/", "/ws-notifications/**", "/ws-notifications").permitAll()
					.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
					.anyRequest().authenticated())
					.httpBasic(Customizer.withDefaults())
					//Makes session Stateless (Creates new session ID every time)
					.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
					.build();
	}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}


