package com.niket.springsecurity.SpringSecurityApplication.model;

import lombok.Data;

@Data
public class AuthenticateRequest {

	private String username;
	private String password;
	
}
