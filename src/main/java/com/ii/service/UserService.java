package com.ii.service;

import org.springframework.stereotype.Service;

import com.ii.object.entity.User;
import com.ii.object.model.DTO.UpdateUserNicknameReqDTO;
import com.ii.object.model.DTO.UserResDTO;
import com.ii.repository.IUserRepository;
import com.ii.utils.SecurityUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final IUserRepository userRepository;
	
	public UserResDTO getUserMe() {
		User me = userRepository.findByUsername(SecurityUtil.getCurrentUsername()
				.orElseThrow(() -> new IllegalArgumentException("로그인 정보 없는데 어캐 접근했음?")));
		UserResDTO userResDTO = UserResDTO.builder()
				.id(me.getId())
				.username(me.getUsername())
				.email(me.getEmail())
				.role(me.getRoles())
				.build();
		return userResDTO;
	}
	
	@Transactional
	public void updateUserNickname(UpdateUserNicknameReqDTO updateUserNicknameReqDTO) {
		
		User me = userRepository.findByUsername(SecurityUtil.getCurrentUsername()
				.orElseThrow(() -> new IllegalArgumentException("로그인 정보 없는데 어캐 접근했음?")));
		me.setNickname(updateUserNicknameReqDTO.getNickname());
		userRepository.save(me);
		
	}

}
