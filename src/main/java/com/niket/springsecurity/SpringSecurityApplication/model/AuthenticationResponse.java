package com.niket.springsecurity.SpringSecurityApplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

	private String accessToken;
	private String refreshToken;
	private String message;
	
}
