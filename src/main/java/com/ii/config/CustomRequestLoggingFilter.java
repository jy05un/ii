package com.ii.config;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;

public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {
	
	@Override
	protected boolean shouldLog(HttpServletRequest request) {
		return true;
	}
	
	@Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        // 아무것도 하지 않음
    }
	
	@Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }
	
}
