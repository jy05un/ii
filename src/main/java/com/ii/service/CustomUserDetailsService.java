package com.ii.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ii.object.entity.User;
import com.ii.object.model.enums.Role;
import com.ii.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService { // 메일 인증이 되어있는 유저만 걸러내기 위한 커스텀 UserDetailsService
	
	private final IUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, IllegalArgumentException {
		
		User user = userRepository.findByUsername(username); // username 기준으로 유저 탐색
		if(user == null) throw new UsernameNotFoundException("Could not found user: " + username); // 유저가 null(=존재하지 않음)이면 유저없음 에러 발생
		
		if(!user.getIsMailAuthed()) throw new IllegalArgumentException("Mail Auth Not Completed Yet!"); // 유저가 메일 인증이 안되어있으면 에러 발생
		
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUsername())
				.password(user.getHashedPassword())
				.authorities(getAuthorities(user.getRoles()))
				.build();
		
	}
	
	private Collection<GrantedAuthority> getAuthorities(String roles) {
		// 문자열로 되어있는 Role, 이를테면 ("USER, ADMIN")을 SimpleGrantedAuthority의 List로 바꾸기 위한 함수
		// ex) "USER, ADMIN" -> "ROLE_USER", "ROLE_ADMIN" of SimpleGrantedAuthority
		
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		
		for(String role : roles.split(", ")) {
			if(StringUtils.hasText(role)) {
				authorities.add(new SimpleGrantedAuthority(Role.findRoleByName(role)));
			}
		}
		
		return authorities;
	}
	
	

}
