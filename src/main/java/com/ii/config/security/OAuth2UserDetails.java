package com.ii.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

import com.ii.object.entity.User;
import com.ii.object.model.enums.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OAuth2UserDetails implements UserDetails, OAuth2User {
	
	private final User user;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		String roles = user.getRoles();
		
		for(String role : roles.split(", ")) {
			if(StringUtils.hasText(role)) {
				authorities.add(new SimpleGrantedAuthority(Role.findRoleByName(role)));
			}
		}
		
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.user.getUsername();
	}
	
	public String getRoles() {
		return this.user.getRoles();
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
