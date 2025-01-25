package com.ii.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ii.object.entity.User;
import com.ii.object.model.DTO.GetIsUsernameExistResDTO;
import com.ii.object.model.DTO.UpdateUserNicknameReqDTO;
import com.ii.object.model.DTO.UserResDTO;
import com.ii.object.model.common.Response;
import com.ii.repository.IUserRepository;
import com.ii.service.UserService;
import com.ii.utils.ResponseUtil;
import com.ii.utils.SecurityUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
	
	private final IUserRepository userRepository;
	private final UserService userService;

	// 테스트용
	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> getUser() {
		
		try {
			UserResDTO userResDTO = userService.getUserMe();
			return ResponseUtil.build(HttpStatus.OK, "get user", userResDTO);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.NOT_FOUND, "user not found", null);
		}
		
	}
	
	@GetMapping("/exists/{username}")
	public ResponseEntity<Response> getIsUsernameExist(@PathVariable("username") String username) {
		
		Boolean isExist = userRepository.existsByUsername(username);
		if(isExist) {
			GetIsUsernameExistResDTO getIsUsernameExistResDTO = GetIsUsernameExistResDTO.builder().exists(isExist).build();
			return ResponseUtil.build(HttpStatus.OK, "", getIsUsernameExistResDTO);
		}
		
		return ResponseUtil.build(HttpStatus.NOT_FOUND, "", null);
		
	}
	
	@PutMapping("/me/nickname")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> updateUserNickname(@RequestBody @Valid UpdateUserNicknameReqDTO updateUserNicknameReqDTO) {
		
		try {
			userService.updateUserNickname(updateUserNicknameReqDTO);
			return ResponseUtil.build(HttpStatus.NO_CONTENT, "유저 수정됨", null);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "요청이 뭔가 이상하네요...", null);
		}
		
	}
	
}
