package com.project.chitFund.module.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.chitFund.module.auth.dto.RegisterRequest;
import com.project.chitFund.module.auth.entity.User;
import com.project.chitFund.module.auth.entity.UserStatus;
import com.project.chitFund.module.auth.entity.UserType;
import com.project.chitFund.module.auth.repository.UserRepository;
import com.project.chitFund.module.customer.entity.Customer;
import com.project.chitFund.module.customer.repository.CustomerRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final OtpService otpService;
	private final PasswordEncoder passwordEncoder;

	public AuthService(UserRepository userRepository, CustomerRepository customerRepository, OtpService otpService, PasswordEncoder passwordEncoder) {

		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
		this.otpService = otpService;
		this.passwordEncoder = passwordEncoder;
	}

	public void registerCustomer(RegisterRequest request) {

		// Mobile or Email must be provided
		if ((request.getMobile() == null || request.getMobile().isBlank())
				&& (request.getEmail() == null || request.getEmail().isBlank())) {

			throw new IllegalArgumentException("Either mobile number or email is required");
		}

		// Check duplicate mobile
		if (request.getMobile() != null && !request.getMobile().isBlank()
				&& userRepository.existsByMobile(request.getMobile())) {

			throw new IllegalArgumentException("Mobile number already registered");
		}

		// Check duplicate email
		if (request.getEmail() != null && !request.getEmail().isBlank()
				&& userRepository.existsByEmail(request.getEmail())) {

			throw new IllegalArgumentException("Email already registered");
		}

		// Create User
		User user = new User();

		user.setMobile(request.getMobile());
		user.setEmail(request.getEmail());
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		user.setUserType(UserType.CUSTOMER);
		user.setStatus(UserStatus.PENDING);
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());

		User savedUser = userRepository.save(user);

		// Create Customer profile
		Customer customer = new Customer();

		customer.setUser(savedUser);
		customer.setName(request.getName());

		customerRepository.save(customer);
		// Generate OTP
		otpService.generateOtp(savedUser);
	}
}