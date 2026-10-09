package com.project.chitFund.module.staff.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.chitFund.module.auth.entity.User;
import com.project.chitFund.module.staff.entity.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long> {

	Optional<Staff> findByUserId(Long userId);

	boolean existsByUserId(Long userId);

	Optional<Staff> findByEmployeeCode(String employeeCode);
	
	Optional<Staff> findByUser(User user);
}
