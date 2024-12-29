package com.ii.config.security;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
	
	private static String ROLE_KEY = "Role";
    private final String secret;
    private final long accessValidityInMilliseconds;
    private final int refreshValidityInMilliseconds;
    private Key key;
    
    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-validity-in-seconds}") long tokenValidityInSeconds,
            @Value("${jwt.refresh-validity-in-days}") int refreshValidityInDays) {
        this.secret = secret;
        this.accessValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.refreshValidityInMilliseconds = refreshValidityInDays * 1000 * 60 * 60 * 24;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String generateAccessToken(String username, String roleString) {
		long now = (new Date()).getTime();
		Date validity = new Date(now + this.accessValidityInMilliseconds);
		return this.generateToken(TokenType.ACCESS, username, roleString, validity);
	}
	
	public String generateRefreshToken(String username) {
		long now = (new Date()).getTime();
		Date validity = new Date(now + this.refreshValidityInMilliseconds);
		return this.generateToken(TokenType.REFRESH, username, "", validity);
	}
	
	public String generateToken(TokenType tokenType, String username, String roleString, Date validity) {

        return Jwts.builder()
                .setSubject(username)
                .claim(ROLE_KEY, roleString) // 정보 저장
                .setIssuedAt(new Date())
                .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret값 세팅
                .setExpiration(validity) // set Expire Time 해당 옵션 안넣으면 expire안함
                .compact();
    }
	
	public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities;
        if(claims.get(ROLE_KEY).equals("")) authorities = Collections.emptyList();
        else {
        	authorities =
                    Arrays.stream(claims.get(ROLE_KEY).toString().split(","))
                    		.map(Role::findRoleByName)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
        }

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
	
	public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
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
	
	public boolean isExpired(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return false;
		} catch (ExpiredJwtException e) {
			return true;
		}
	}
	
}
