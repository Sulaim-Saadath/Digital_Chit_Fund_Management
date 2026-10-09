package com.project.chitFund.module.auth.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.project.chitFund.module.auth.entity.OtpVerification;
import com.project.chitFund.module.auth.entity.User;
import com.project.chitFund.module.auth.repository.OtpVerificationRepository;

@Service
public class OtpService {

	private final OtpVerificationRepository otpVerificationRepository;

	public OtpService(OtpVerificationRepository otpVerificationRepository) {
		this.otpVerificationRepository = otpVerificationRepository;
	}

	public OtpVerification generateOtp(User user) {

		Random random = new Random();

		String otp = String.format("%06d", random.nextInt(1000000));

		OtpVerification otpVerification = new OtpVerification();

		otpVerification.setUser(user);
		otpVerification.setOtp(otp);
		otpVerification.setCreatedAt(LocalDateTime.now());
		otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
		otpVerification.setAttempts(0);
		otpVerification.setMaxAttempts(3);
		otpVerification.setVerified(false);

		OtpVerification savedOtp = otpVerificationRepository.save(otpVerification);

		// Simulated OTP delivery
		System.out.println("OTP for user " + user.getId() + " : " + otp);

		return savedOtp;
	}

	public boolean verifyOtp(User user, String submittedOtp) {

		OtpVerification otpVerification = otpVerificationRepository.findTopByUserOrderByCreatedAtDesc(user)
				.orElseThrow(() -> new RuntimeException("OTP not found"));

		// 1. Check if OTP is already verified
		if (otpVerification.isVerified()) {
			throw new RuntimeException("OTP already verified");
		}

		// 2. Check maximum attempts
		if (otpVerification.getAttempts() >= otpVerification.getMaxAttempts()) {
			throw new RuntimeException("Maximum OTP attempts exceeded");
		}

		// 3. Check expiry
		if (LocalDateTime.now().isAfter(otpVerification.getExpiresAt())) {
			throw new RuntimeException("OTP has expired");
		}

		// 4. Compare OTP
		if (!otpVerification.getOtp().equals(submittedOtp)) {

			// Wrong OTP → increase attempts
			otpVerification.setAttempts(otpVerification.getAttempts() + 1);

			otpVerificationRepository.save(otpVerification);

			throw new RuntimeException("Invalid OTP");
		}

		// 5. Correct OTP
		otpVerification.setVerified(true);
		otpVerificationRepository.save(otpVerification);
		return true;
	}
}
