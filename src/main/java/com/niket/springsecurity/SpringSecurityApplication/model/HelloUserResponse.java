package com.niket.springsecurity.SpringSecurityApplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HelloUserResponse {
	
	private String desc;
	private Integer status;

}
