
package com.project.chitFund.module.auth.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.project.chitFund.module.auth.entity.OtpPurpose;
import com.project.chitFund.module.auth.entity.OtpVerification;
import com.project.chitFund.module.auth.entity.User;
import com.project.chitFund.module.auth.repository.OtpVerificationRepository;

@Service
public class OtpService {

    private final OtpVerificationRepository otpVerificationRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(OtpVerificationRepository otpVerificationRepository) {
        this.otpVerificationRepository = otpVerificationRepository;
    }

    public OtpVerification generateOtp(User user, OtpPurpose purpose) {

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));

        OtpVerification otpVerification = new OtpVerification();

        otpVerification.setUser(user);
        otpVerification.setPurpose(purpose);
        otpVerification.setOtp(otp);
        otpVerification.setCreatedAt(LocalDateTime.now());
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpVerification.setAttempts(0);
        otpVerification.setMaxAttempts(3);
        otpVerification.setVerified(false);

        OtpVerification savedOtp = otpVerificationRepository.save(otpVerification);

        // Simulated OTP delivery for development
        System.out.println("OTP purpose: " + purpose
                + ", user ID: " + user.getId()
                + ", OTP: " + otp);

        return savedOtp;
    }

    public boolean verifyOtp(User user, String submittedOtp, OtpPurpose purpose) {

        OtpVerification otpVerification =
                otpVerificationRepository
                        .findTopByUserAndPurposeOrderByCreatedAtDesc(user, purpose)
                        .orElseThrow(() -> new IllegalArgumentException("OTP not found"));

        if (otpVerification.isVerified()) {
            throw new IllegalArgumentException("OTP already verified");
        }

        if (otpVerification.getAttempts() >= otpVerification.getMaxAttempts()) {
            throw new IllegalArgumentException("Maximum OTP attempts exceeded");
        }

        if (LocalDateTime.now().isAfter(otpVerification.getExpiresAt())) {
            throw new IllegalArgumentException("OTP has expired");
        }

        if (!otpVerification.getOtp().equals(submittedOtp)) {
            otpVerification.setAttempts(otpVerification.getAttempts() + 1);
            otpVerificationRepository.save(otpVerification);
            throw new IllegalArgumentException("Invalid OTP");
        }

        otpVerification.setVerified(true);
        otpVerificationRepository.save(otpVerification);

        return true;
    }
}
