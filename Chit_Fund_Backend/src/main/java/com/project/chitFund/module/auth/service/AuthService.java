package com.project.chitFund.module.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.chitFund.module.auth.dto.LoginRequest;
import com.project.chitFund.module.auth.dto.LoginResponse;
import com.project.chitFund.module.auth.dto.MeResponse;
import com.project.chitFund.module.auth.dto.RegisterRequest;
import com.project.chitFund.module.auth.entity.User;
import com.project.chitFund.module.auth.entity.UserStatus;
import com.project.chitFund.module.auth.entity.UserType;
import com.project.chitFund.module.auth.repository.UserRepository;
import com.project.chitFund.module.auth.security.JwtService;
import com.project.chitFund.module.customer.entity.Customer;
import com.project.chitFund.module.customer.repository.CustomerRepository;
import com.project.chitFund.module.staff.repository.StaffRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final OtpService otpService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final StaffRepository staffRepository;

	public AuthService(UserRepository userRepository, CustomerRepository customerRepository, OtpService otpService,
			PasswordEncoder passwordEncoder, JwtService jwtService, StaffRepository staffRepository) {

		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
		this.otpService = otpService;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.staffRepository = staffRepository;
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

	public void verifyCustomerOtp(String mobile, String otp) {

		// Find user using mobile number
		User user = userRepository.findByMobile(mobile).orElseThrow(() -> new RuntimeException("User not found"));

		// Verify OTP
		otpService.verifyOtp(user, otp);

		// OTP verified successfully
		user.setStatus(UserStatus.ACTIVE);

		user.setUpdatedAt(LocalDateTime.now());

		userRepository.save(user);
	}

	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail()).orElse(null);
		if (user == null) {
			throw new IllegalArgumentException("Invalid email or password");
		}

		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new IllegalArgumentException("User account is not active");
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Invalid email or password");
		}
		String token = jwtService.generateToken(user);
		String role = null;
		if (user.getUserType() == UserType.STAFF) {
			role = staffRepository.findByUser(user)
					.orElseThrow(() -> new IllegalArgumentException("Staff record not found")).getRole().name();
		}
		return new LoginResponse(token, user.getUserType().name(), role);
	}

	public MeResponse getCurrentUser(String userId) {
		Long id = Long.parseLong(userId);
		User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
		String role = null;
		if (user.getUserType() == UserType.STAFF) {
			role = staffRepository.findByUser(user)
					.orElseThrow(() -> new IllegalArgumentException("Staff record not found")).getRole().name();
		}
		return new MeResponse(user.getId(), user.getEmail(), user.getUserType().name(), role);
	}
}