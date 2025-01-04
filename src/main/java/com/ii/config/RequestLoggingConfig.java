package com.ii.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestLoggingConfig {

	/*
	 * HTTP 요청에 대한 기본 로깅 메세지 설정
	 */
	
    @Bean
    CustomRequestLoggingFilter logFilter() {
    	CustomRequestLoggingFilter filter = new CustomRequestLoggingFilter();
        filter.setIncludeQueryString(true);  // 쿼리 스트링 포함
        filter.setIncludePayload(false);      // 요청 바디 제외 (비밀번호 등의 페이로드 포함되어있음)
        filter.setIncludeHeaders(false);     // 요청 헤더 제외
        filter.setMaxPayloadLength(10000);   // 최대 바디 길이
        filter.setAfterMessagePrefix("HTTP REQUEST : ");	// HTTP 요청 처리 후에 남는 로그 메세지에 대한 prefix
        return filter;
    }
    
}