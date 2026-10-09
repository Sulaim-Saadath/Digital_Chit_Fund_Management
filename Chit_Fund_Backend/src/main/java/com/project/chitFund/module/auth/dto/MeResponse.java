package com.project.chitFund.module.auth.dto;

public class MeResponse {

	private Long id;
	private String email;
	private String userType;
	private String role;

	public MeResponse(Long id, String email, String userType, String role) {
		this.id = id;
		this.email = email;
		this.userType = userType;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getUserType() {
		return userType;
	}

	public String getRole() {
		return role;
	}
}