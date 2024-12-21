package com.ii.service;

import java.util.Optional;

import org.hibernate.internal.ExceptionConverterImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ii.object.entity.User;
import com.ii.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final IUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws IllegalArgumentException {
		User user = Optional.ofNullable(userRepository.findByUsername(username))
				.orElseThrow(() -> new IllegalArgumentException("Could not found user" + username));
		
		if(!user.getMailAuth()) throw new IllegalArgumentException("Mail Auth Not Completed Yet!");
		
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUsername())
				.password(user.getHashedPassword())
				.authorities(user.getRole())
				.build();
		
	}
	
	

}
