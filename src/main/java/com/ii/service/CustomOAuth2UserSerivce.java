package com.ii.service;

import java.util.HashMap;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ii.config.security.OAuth2UserDetails;
import com.ii.object.entity.RefreshToken;
import com.ii.object.entity.User;
import com.ii.repository.IRefreshTokenRepository;
import com.ii.repository.IUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserSerivce extends DefaultOAuth2UserService {
	
	private final IUserRepository userRepository;
	private final IRefreshTokenRepository refreshTokenRepository;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
		
		DefaultOAuth2User oAuth2User = (DefaultOAuth2User)super.loadUser(oAuth2UserRequest);
		
		User user = handleOAuth2UserRequest(oAuth2UserRequest, oAuth2User);

		return new OAuth2UserDetails(user);
		
	}
	
	@Transactional
	private User handleOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest, DefaultOAuth2User oAuth2User) {
		
		String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
		HashMap<String, Object> userInfo = oAuth2User.getAttribute("response");
		String oAuth2UserId = userInfo.get("id").toString();
		if(oAuth2UserId == null) return null;
		
		Boolean isUserExist = userRepository.existsByOAuth2IdAndOAuth2Type(oAuth2UserId, registrationId);
		
		if(!isUserExist) {
			User newUser = User.builder()
					.OAuth2Id(oAuth2UserId)
					.OAuth2Type(registrationId)
					.mailAuth(true)
					.build();
			RefreshToken refreshToken = RefreshToken.builder()
					.user(newUser)
					.build();
			User createdUser = userRepository.save(newUser);
			refreshTokenRepository.save(refreshToken);
			return createdUser;
		}
		
		return userRepository.findByOAuth2IdAndOAuth2Type(oAuth2UserId, registrationId);
		
	}

}
