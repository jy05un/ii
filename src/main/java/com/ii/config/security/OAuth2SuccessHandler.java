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
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
	private final TokenProvider tokenProvider;
	private final IRefreshTokenRepository refreshTokenRepository;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2UserDetails oAuth2User = (OAuth2UserDetails) authentication.getPrincipal();
		
		String accessTokenString = tokenProvider.generateAccessToken(oAuth2User.getUsername(), oAuth2User.getRoles());
		UsernamePasswordAuthenticationToken accessAuthenticationToken = tokenProvider.getAuthentication(accessTokenString);
		SecurityContextHolder.getContext().setAuthentication(accessAuthenticationToken);
		
		String refreshTokenString = tokenProvider.generateRefreshToken(oAuth2User.getUsername());
		
		RefreshToken refreshToken = refreshTokenRepository.getByuserUsername(oAuth2User.getUsername());
		refreshToken.setToken(refreshTokenString);
		refreshTokenRepository.save(refreshToken);
//		
//		return Pair.of(accessTokenString, newRefreshTokenString);
		
		response.addHeader(SecurityUtil.AUTHORIZATION_HEADER, "Bearer: " + accessTokenString);
		response.addHeader(HttpHeaders.SET_COOKIE, SecurityUtil.parseRefreshCookie(refreshTokenString));
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		Response responseJSON = new Response(HttpStatus.OK, "login success, jwt successfully provided", null);
		String resJSONString = new ObjectMapper().writeValueAsString(responseJSON);
		response.getWriter().write(resJSONString);
		
		return;
		
		
	}

}
