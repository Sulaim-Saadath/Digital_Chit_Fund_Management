package com.project.chitFund.module.auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.project.chitFund.module.auth.entity.User;
import com.project.chitFund.module.staff.entity.Staff;
import com.project.chitFund.module.staff.repository.StaffRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
	private long expiration;
	private final StaffRepository staffRepository;

	public JwtService(StaffRepository staffRepository) {
		this.staffRepository = staffRepository;
	}

	private SecretKey getSigningKey() {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(User user) {
		var builder = Jwts.builder().subject(user.getId().toString()).claim("userType", user.getUserType().name())
				.issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expiration));
		if (user.getUserType().name().equals("STAFF")) {
			Staff staff = staffRepository.findByUser(user)
					.orElseThrow(() -> new RuntimeException("Staff record not found"));
			builder.claim("role", staff.getRole().name());
		}
		return builder.signWith(getSigningKey()).compact();
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}

	public String extractUserId(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String extractUserType(String token) {
		return extractAllClaims(token).get("userType", String.class);
	}

	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	public boolean isTokenValid(String token) {
		try {
			extractAllClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}