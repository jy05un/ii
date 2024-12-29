package com.ii.utils;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class SecurityUtil {
	
	public static final String AUTHORIZATION_HEADER = "Authorization";
	
	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:',.<>?/";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;
    private static final int LENGTH = 10;

    public static String generateSecureString() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Add one character from each required category
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the rest of the password length with random characters
        for (int i = 4; i < LENGTH; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle the characters to ensure randomness
        List<Character> passwordChars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            passwordChars.add(c);
        }
        Collections.shuffle(passwordChars, random);

        StringBuilder finalPassword = new StringBuilder();
        for (char c : passwordChars) {
            finalPassword.append(c);
        }

        return finalPassword.toString();
    }

	public static Optional<String> getCurrentUsername() {

        // authentication객체가 저장되는 시점은 JwtFilter의 doFilter 메소드에서 
        // Request가 들어올 때 SecurityContext에 Authentication 객체를 저장해서 사용
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            System.out.println("인증 정보 없음, 로그인 하셈 ㅡㅡ");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }
	
	// Request Header 에서 토큰 정보를 꺼내오기 위한 메소드
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
	
	public static String parseRefreshCookie(String refreshToken) {
		ResponseCookie cookie = ResponseCookie
				.from("Refresh", refreshToken)
				.domain("localhost")
				.httpOnly(true)
				.secure(false)
				// .secure(true)
				.maxAge(Duration.ofHours(12))
				.build();
		return cookie.toString();
	}
	
}
