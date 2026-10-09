package com.project.chitFund.module.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.chitFund.module.auth.dto.LoginRequest;
import com.project.chitFund.module.auth.dto.LoginResponse;
import com.project.chitFund.module.auth.dto.MeResponse;
import com.project.chitFund.module.auth.dto.RegisterOtpRequest;
import com.project.chitFund.module.auth.dto.RegisterRequest;
import com.project.chitFund.module.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
		try {
			authService.registerCustomer(request);
			return ResponseEntity.ok("Registration successful");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestBody RegisterOtpRequest request) {
		try {
			authService.verifyCustomerOtp(request.getMobile(), request.getOtp());
			return ResponseEntity.ok("OTP verified successfully");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		try {
			LoginResponse response = authService.login(request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(Authentication authentication) {
		try {
			MeResponse response = authService.getCurrentUser(authentication.getName());
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}