package com.ii.utils;

import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ii.object.model.common.Response;

@Component
public class ResponseUtil {

	public static ResponseEntity<Response> build(HttpStatus httpStatus, String msg, Object body,
			String accessTokenString, String refreshTokenString) {
		Response response = new Response(httpStatus, msg, body);
		
		HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		httpHeaders.add(SecurityUtil.AUTHORIZATION_HEADER, "Bearer: " + accessTokenString);
		httpHeaders.add(HttpHeaders.SET_COOKIE, SecurityUtil.parseRefreshCookie(refreshTokenString));
		return new ResponseEntity<>(response, httpHeaders, httpStatus);
	}
	
	public static ResponseEntity<Response> build(HttpStatus httpStatus, String msg, Object body) {
		Response response = new Response(httpStatus, msg, body);
		
		HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		return new ResponseEntity<>(response, headers, httpStatus);
	}
	
}
