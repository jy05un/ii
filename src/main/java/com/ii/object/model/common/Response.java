package com.ii.object.model.common;

import org.springframework.http.HttpStatus;

import com.ii.object.model.DTO.RegisterResDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class Response {
	
	private HttpStatus status;
	private String message;
	private Object data;

}
