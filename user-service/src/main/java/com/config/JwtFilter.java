package com.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.service.JwtService;
import com.service.UserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private ApplicationContext context;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {
	    String authHeader = request.getHeader("Authorization");
	    String token = null;
	    String username = null;
	    
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        token = authHeader.substring(7);
	        username = jwtService.getUserName(token);

	        Claims claims = jwtService.extractAllClaims(token);
	        if ("post-service".equals(username) && "SERVICE".equals(claims.get("role"))) {
	            SecurityContextHolder.getContext().setAuthentication(
	                new UsernamePasswordAuthenticationToken("post-service", null, List.of(new SimpleGrantedAuthority("ROLE_SERVICE")))
	            );
	            filterChain.doFilter(request, response);
	            return;
	        }
	    }

	    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        UserDetails userDetails = context.getBean(UserService.class).loadUserByUsername(username);
	        
	        if (jwtService.validate(token, userDetails)) {
	            UsernamePasswordAuthenticationToken authToken = 
	                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(authToken);
	        }
	    }
	    filterChain.doFilter(request, response);
	}
}
