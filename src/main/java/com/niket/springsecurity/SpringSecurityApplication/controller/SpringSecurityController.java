package com.niket.springsecurity.SpringSecurityApplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niket.springsecurity.SpringSecurityApplication.config.MyUserDetailsService;
import com.niket.springsecurity.SpringSecurityApplication.model.AuthenticateRequest;
import com.niket.springsecurity.SpringSecurityApplication.model.AuthenticationResponse;
import com.niket.springsecurity.SpringSecurityApplication.model.HelloUserResponse;
import com.niket.springsecurity.SpringSecurityApplication.model.RefreshTokenRequest;
import com.niket.springsecurity.SpringSecurityApplication.utils.IOJwtUtil;

@RestController
@RequestMapping(value = "/security")
public class SpringSecurityController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	MyUserDetailsService myUserDetailsService;

	@Autowired
	IOJwtUtil jwtUtil;

	@PostMapping(value = "/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest authenticateRequest) {
		System.out.println(authenticateRequest);
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticateRequest.getUsername(), authenticateRequest.getPassword()));
		} catch (AuthenticationException exception) {
			exception.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthenticationResponse("", "", "Access Denied"));
		}
		UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticateRequest.getUsername());
		String accessToken = jwtUtil.generateToken(userDetails, false);
		String refreshToken = jwtUtil.generateToken(userDetails, true);
		return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken, "Success"));
	}

	@PostMapping(value = "/refreshtoken")
	public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		System.out.println(refreshTokenRequest.getRefreshToken());
		String username = jwtUtil.extractUsername(refreshTokenRequest.getRefreshToken());
		if(!username.contains("RefreshToken")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthenticationResponse("", "", "Wrong Refresh Token"));
		}
		UserDetails userDetails = myUserDetailsService
				.loadUserByUsername(username.substring(0, username.lastIndexOf(" ")));
		String accessToken = jwtUtil.generateToken(userDetails, false);
		return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshTokenRequest.getRefreshToken(), "Success"));
	}

	@GetMapping(value = "/hello")
	public String hello() {
		return "<h1>Hello World</h1>";
	}

	@GetMapping(value = "/user")
	public HelloUserResponse helloUser() {
		return new HelloUserResponse("Hello User",200);
	}

	@GetMapping(value = "/admin")
	public String helloAdmin() {
		return "<h1>Hello Admin</h1>";
	}

}
