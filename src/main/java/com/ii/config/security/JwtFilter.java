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
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	
    private final TokenProvider tokenProvider;
    // /auth/**로 오는 요청들은 Jwt를 따로 검증하지 않음
    private final AntPathRequestMatcher excludePath = new AntPathRequestMatcher("/auth/**");
    // 하지만 그 중에서도 아래의 3개의 url은 권한이 필요한 요청이므로 검증해야 되는 요청들임
    private final List<RequestMatcher> excludeExceptionPaths = List.of(
    			new AntPathRequestMatcher("/auth/logout", HttpMethod.GET.name()),
	    		new AntPathRequestMatcher("/auth/unregister", HttpMethod.DELETE.name()),
	    		new AntPathRequestMatcher("/auth/email", HttpMethod.PUT.name()),
	    		new AntPathRequestMatcher("/auth/password/update", HttpMethod.POST.name())
    		);
    
    /**
     * ServletRequest가 위에서 설정한 excludePath에 포함되면서 excludeExceptionPaths 중 하나가 아닐 경우, 즉 jwt 검증이 필요한지 안 한지를 판단
     */
    private boolean matchExcludePaths(HttpServletRequest request) {
    	
    	if(excludeExceptionPaths.stream().anyMatch(matcher -> matcher.matches(request))) {	// excludeExceptionPaths 중 하나라도 일치하면
    		return false;																	// false 반환 = JWT 검증 필요
    	}
    	if(excludePath.matches(request)) return true;	// excludePath와 매치되면 true 반환 = JWT 검증 불필요
    	
    	return false;	// 기본으로 모든 요청들은 JWT 검증이 필요함
    }

    // 실제 JWT 검증 & SecurityContext에 인증 정보를 저장하는 역할을 함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
    	if(matchExcludePaths(request)) {	// 검증 예외 대상 요청이라면 검증 로직을 패스함
    		filterChain.doFilter(request, response);
    		return;
    	}
    	
        String jwt = SecurityUtil.resolveToken(request);	// 요청 헤더(Authorization)에서 AccessToken을 파싱
        String requestURI = request.getRequestURI();	// 로깅을 위한 요청 uri 파싱

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {	// jwt가 비어있지 않음 && 유효한 jwt임
            Authentication authentication = tokenProvider.getAuthentication(jwt);	// jwt에서 인증정보를 가져옴
            SecurityContextHolder.getContext().setAuthentication(authentication);	// SecurityContext에 인증정보를 저장해서 전역에서 사용할 수 있게함
            log.info("Security Context에 '{}/{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), authentication.getAuthorities().toString(), requestURI);
        } else {
        	log.info("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(request, response);
        return;
    }

}