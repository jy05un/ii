package com.ii.object.model.common;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
	
	/*
	 * 기본 응답 오브젝트
	 */
	
	private HttpStatus status;	// 응답 상태코드
	private String message;		// 응답 메세지
	private Object data;		// 응답 JSON 데이터

}
