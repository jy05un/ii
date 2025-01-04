package com.ii.config;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.ii.utils.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;

public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {
	
	/*
	 * HTTP 요청에 대한 로깅 설정
	 */
	
	@Override
	protected boolean shouldLog(HttpServletRequest request) {
		return true;	// HTTP 요청에 대해 항상 로그를 남기도록 설정함
	}
	
	@Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        // 요청 처리 전에 아무 로그도 남기지 않음
    }
	
	@Override
    protected void afterRequest(HttpServletRequest request, String message) {
		// 요청 처리 후 요청을 남긴 사용자의 유저네임과 기본 로깅 메세지를 남김
        logger.info("[" + SecurityUtil.getCurrentUsername().get() + "]: " + message);
    }
	
}
