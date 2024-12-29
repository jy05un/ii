package com.ii.config.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ii.utils.SecurityUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	
    private final TokenProvider tokenProvider;
    private final AntPathRequestMatcher excludePath = new AntPathRequestMatcher("/auth/**");
    private final List<RequestMatcher> excludeExceptionPaths = List.of(
	    		new AntPathRequestMatcher("/auth/unregister", HttpMethod.DELETE.name()),
	    		new AntPathRequestMatcher("/auth/email", HttpMethod.PUT.name()),
	    		new AntPathRequestMatcher("/auth/password/update", HttpMethod.POST.name())
    		);
    
    private boolean matchExcludePaths(HttpServletRequest request) {
    	
    	if(excludeExceptionPaths.stream().anyMatch(matcher -> matcher.matches(request))) {
    		return false;
    	}
    	if(excludePath.matches(request)) return true;
    	
    	return false;
    }

    // 실제 필터릴 로직
    // 토큰의 인증정보를 SecurityContext에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
    	if(matchExcludePaths(request)) {
    		filterChain.doFilter(request, response);
    		return;
    	}
    	
        String jwt = SecurityUtil.resolveToken(request);
        String requestURI = request.getRequestURI();

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.printf("Security Context에 '{%s}/{%s}' 인증 정보를 저장했습니다, uri: {%s}\n", authentication.getName(), authentication.getAuthorities().toString(), requestURI);
        } else {
        	System.out.printf("유효한 JWT 토큰이 없습니다, uri: {%s}\n", requestURI);
        }

        filterChain.doFilter(request, response);
        return;
    }

}