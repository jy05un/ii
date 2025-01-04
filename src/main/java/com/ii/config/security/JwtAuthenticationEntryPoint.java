package com.ii.config.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ii.object.model.common.Response;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Role 확인이 되어야하는 요청에 대해 로그인 없이 요청 => 401
    	response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		Response responseJSON = new Response(HttpStatus.UNAUTHORIZED, "You got no authorization information", null);
		String resJSONString = new ObjectMapper().writeValueAsString(responseJSON);
		response.getWriter().write(resJSONString);
    }
}