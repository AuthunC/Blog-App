package com.service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {
	
//	private String secretKey = "Dn3QDCmL+su6AZ2WqLLwjRejKn/RkJpwwr63f+6Evw0=";

	private String secretKey = "macO66Hd7ehS9BI+SF+R6ATCz3Yj0KsSe9vYv5xunF0=";
//	public JwtService() {
//		try {
//			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//			SecretKey key = keyGen.generateKey();
//			secretKey = Base64.getEncoder().encodeToString(key.getEncoded());
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//	}
	
	public String generateJwtToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		
		return Jwts
				.builder()
				.claims()
				.add(claims)
				.subject(username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
				.and()
				.signWith(getKey())
				.compact();
	}
	
	// For post-service
	public String generateServiceToken() {
	    Map<String, Object> claims = new HashMap<>();
	    claims.put("role", "SERVICE");
	    
	    return Jwts.builder()
	        .setClaims(claims)
	        .setSubject("post-service")
	        .setIssuedAt(new Date(System.currentTimeMillis()))
	        .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) 
	        .signWith(getKey())
	        .compact();
	}
	
	@PostConstruct
    public void init() {
        String serviceToken = generateServiceToken();
        System.out.println("Service Token for post-service: " + serviceToken);
    }


	public SecretKey getKey() {
		byte[] decode = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(decode);
	}

	public String getUserName(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public boolean validate(String token, UserDetails userDetails) {
		final String userName = getUserName(token);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

}
