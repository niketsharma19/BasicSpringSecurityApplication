package com.niket.springsecurity.SpringSecurityApplication.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.niket.springsecurity.SpringSecurityApplication.config.MyUserDetailsService;
import com.niket.springsecurity.SpringSecurityApplication.utils.IOJwtUtil;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

	@Autowired
	IOJwtUtil jwtUtil;

	@Autowired
	MyUserDetailsService myUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if(request.getRequestURI().contains("/authenticate") || 
				request.getRequestURI().contains("/refreshtoken")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = request.getHeader("Authorization");
		String username = null;

		if (token != null && token.contains("Bearer")) {
			token = token.substring(7);
			username = jwtUtil.extractUsername(token);
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
			if (jwtUtil.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			} else {
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

}
