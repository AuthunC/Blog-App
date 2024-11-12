package com.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private UserDetailsService userDetailsService;
	private JwtFilter jwtFilter;
	
    public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter=jwtFilter;
    }

	@Bean
	protected SecurityFilterChain customConfig(HttpSecurity http) throws Exception {
		
		return http
				.csrf(customizer->customizer.disable())
				.authorizeHttpRequests(
					req->req
					.requestMatchers("api/users/register", "api/users/login").permitAll()
					.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
					.anyRequest().authenticated())
					.httpBasic(Customizer.withDefaults())
					//Makes session Stateless (Creates new session ID every time)
					.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
					.build();
	}

    @Bean
    public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setPasswordEncoder(new BCryptPasswordEncoder(12));
		auth.setUserDetailsService(userDetailsService);
		return auth;
	}
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    	return config.getAuthenticationManager();
    }
}