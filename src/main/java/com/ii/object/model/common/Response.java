package com.ii.object.model.common;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
	
	private HttpStatus status;
	private String message;
	private Object data;

}
