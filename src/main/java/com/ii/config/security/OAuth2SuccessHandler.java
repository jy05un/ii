package com.ii.config.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ii.object.entity.RefreshToken;
import com.ii.object.model.common.Response;
import com.ii.repository.IRefreshTokenRepository;
import com.ii.utils.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
	private final TokenProvider tokenProvider;
	private final IRefreshTokenRepository refreshTokenRepository;
	
	@Transactional
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// oauth2 로그인 성공시 인증정보를 OAuth2UserDetails 클래스에 담음
		OAuth2UserDetails oAuth2User = (OAuth2UserDetails) authentication.getPrincipal();
		
		// AccessTokenString 생성
		String accessTokenString = tokenProvider.generateAccessToken(oAuth2User.getUsername(), oAuth2User.getRoles());
		// SecurityContext에 저장하기 위한 인증정보를 가져옴
		UsernamePasswordAuthenticationToken accessAuthenticationToken = tokenProvider.getAuthentication(accessTokenString);
		SecurityContextHolder.getContext().setAuthentication(accessAuthenticationToken);
		
		// RefreshTokenString 생성
		String refreshTokenString = tokenProvider.generateRefreshToken(oAuth2User.getUsername());
		
		// RefreshToken 저장소에서 RefreshToken 최신화
		RefreshToken refreshToken = refreshTokenRepository.findByuserUsername(oAuth2User.getUsername());
		refreshToken.setToken(refreshTokenString);
		refreshTokenRepository.save(refreshToken);
		
		// 응답 헤더 중 Authorization에 AccessToken을 담음
		response.addHeader(SecurityUtil.AUTHORIZATION_HEADER, "Bearer: " + accessTokenString);
		// RefreshToken을 쿠키로 설정
		response.addHeader(HttpHeaders.SET_COOKIE, SecurityUtil.parseRefreshCookie(refreshTokenString, 15));
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		Response responseJSON = new Response(HttpStatus.OK, "login success, jwt successfully provided", null);
		String resJSONString = new ObjectMapper().writeValueAsString(responseJSON);
		response.getWriter().write(resJSONString);
		
		return;
		
	}

}
