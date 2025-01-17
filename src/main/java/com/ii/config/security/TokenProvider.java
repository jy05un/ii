package com.ii.config.security;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.ii.object.model.enums.Role;
import com.ii.object.model.enums.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenProvider implements InitializingBean {
	
	private static String ROLE_KEY = "Role";	// Jwt 토큰에서 권한(=Role)을 가져오기 위한 KEY
	private static String TYPE_KEY = "Type";	// Jwt 토큰에서 타입을 가져오기 위한 KEY
	private static String DEVICE_ID_KEY = "Device_Id";
    private final String secret;	// Jwt 암호화 코드
    private final long accessValidityInMilliseconds;	// AccessToken 유효기간 (ms단위)
    private final int refreshValidityInMilliseconds;	// RefreshToken 유효기간 (ms단위)
    private Key key;	// 실제 토큰 서명에 사용되는 key, secret을 base64 디코딩 한 후 HMAC SHA 알고리즘을 사용하여 값을 생성함
    
    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-validity-in-seconds}") long tokenValidityInSeconds,
            @Value("${jwt.refresh-validity-in-days}") int refreshValidityInDays) {
        this.secret = secret;
        this.accessValidityInMilliseconds = tokenValidityInSeconds * 1000;	// 프로퍼티 파일에 설정한 access token 유효기간(Sec 단위)를 ms 단위로 변경
        this.refreshValidityInMilliseconds = refreshValidityInDays * 1000 * 60 * 60 * 24;	// refresh token 유효기간(Day 단위)를 ms 단위로 변경
    }

    /**
     * 서명을 위한 비밀키를 생성함
     */
	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
	}
	
	/**
	 * Access Token을 생성함
	 * @param roleString (ex. "ADMIN,USER")
	 */
	public String generateAccessToken(String username, String roleString, UUID deviceId) {
		long now = (new Date()).getTime();
		Date validity = new Date(now + this.accessValidityInMilliseconds);	// 현재 시간을 기준으로 Access Token 만료시점을 설정함
		
		return Jwts.builder()
                .setSubject(username)	// subject = username
                .claim(TYPE_KEY, TokenType.ACCESS)
                .claim(ROLE_KEY, roleString) // 권한(=Role) 저장
                .claim(DEVICE_ID_KEY, deviceId)	// 다중 로그인 구별을 위한 디바이스 아이디
                .setIssuedAt(new Date())	// 토큰 발행 시점
                .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 서명할 때 사용할 비밀키를 인자로 넘겨 서명
                .setExpiration(validity) // 토큰 만료 시점을 설정함
                .compact();
	}
	
	/**
	 * Refresh Token을 생성
	 */
	public String generateRefreshToken(String username, UUID deviceId) {
		long now = (new Date()).getTime();
		Date validity = new Date(now + this.refreshValidityInMilliseconds);	// 현재 시간을 기준으로 Refresh Token 만료시점을 설정함
		return Jwts.builder()
                .setSubject(username)	// subject = username
                .claim(TYPE_KEY, TokenType.REFRESH)
                .claim(ROLE_KEY, "") // 권한 검증은 필요하지 않음
                .claim(DEVICE_ID_KEY, deviceId)	// 다중 로그인 구별을 위한 디바이스 아이디
                .setIssuedAt(new Date())	// 토큰 발행 시점
                .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 서명할 때 사용할 비밀키를 인자로 넘겨 서명
                .setExpiration(validity) // 토큰 만료 시점을 설정함
                .compact();
	}
	
	/*
	 * 토큰에서 인증정보를 가져옴
	 */
	public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = Jwts	// 토큰 생성 시에 설정한 값들 (ex. Role)
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities;	// 해당 토큰이 가지는 권한(=Role)들
        if(claims.get(ROLE_KEY).equals("")) authorities = Collections.emptyList();	// Role 값이 비어있으면 빈 권한을 반환
        else {
        	authorities =
                    Arrays.stream(claims.get(ROLE_KEY).toString().split(","))	// USER,ADMIN 형태의 role string을 ","를 기준으로 쪼개서 나눔
                    		.map(Role::findRoleByName)			// "USER" 형태의 값을 "ROLE_USER" 형태로 매핑하여 SimpleGrantedAuthority가 요구하는 형식을 맞춤
                            .map(SimpleGrantedAuthority::new)	// 권한 생성
                            .collect(Collectors.toList());		// 생성된 권한들을 묶어 authorities로 저장
        }

        User principal = new User(claims.getSubject(), "", authorities);
        // username과 권한들을 담아 Spring Security가 제공하는 기본 UserDetail 생성, 이때 추후 로직에서 사용하지 않을 비밀번호는 담지 않음

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);	// 인증정보 생성하여 반환
    }
	
	/*
	 * 토큰이 유효함을 검사함
	 */
	public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);	// 생성했던 key를 이용해 jwt가 옳게 서명되었는지 검증하고 claim들을 가져옴
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("잘못 서명된 토큰");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 토큰");
        } catch (IllegalArgumentException e) {
        	System.out.println("잘못된 토큰");
        }
        return false;
    }
	
	/*
	 * 해당 토큰이 만료되었는지만을 검사하는 함수
	 */
	public boolean isExpired(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return false;
		} catch (ExpiredJwtException e) {
			return true;
		}
	}
	
	public UUID getDeviceId(String token) {
		return UUID.fromString(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get(DEVICE_ID_KEY).toString());
	}
	
}
