package com.project.chitFund.module.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.chitFund.module.auth.dto.LoginRequest;
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
	public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

		authService.registerCustomer(request);

		return ResponseEntity.ok("Customer registration initiated");
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestBody RegisterOtpRequest request) {

		authService.verifyCustomerOtp(request.getMobile(), request.getOtp());

		return ResponseEntity.ok("OTP verified successfully. Registration completed.");
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest request) {

		String response = authService.login(request);
		if (response.equals("Login successful")) {
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.badRequest().body(response);
	}
}