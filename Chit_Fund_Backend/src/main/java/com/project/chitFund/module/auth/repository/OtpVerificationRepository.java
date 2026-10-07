package com.project.chitFund.module.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.chitFund.module.auth.entity.OtpVerification;
import com.project.chitFund.module.auth.entity.User;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

	Optional<OtpVerification> findTopByUserOrderByCreatedAtDesc(User user);
}
