package com.ii.controller;

import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ii.object.entity.User;
import com.ii.object.model.DTO.UserResDTO;
import com.ii.object.model.common.Response;
import com.ii.repository.IUserRepository;
import com.ii.service.AuthService;
import com.ii.utils.SecurityUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
	
	private final IUserRepository userRepository;
	private final SecurityUtil securityUtil;

	@GetMapping("/user")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<Response> getUser() {
		User me = userRepository.findByUsername(securityUtil.getCurrentUsername()
				.orElseThrow(() -> new IllegalArgumentException("로그인 정보 없는데 어캐 접근했음?")));
		UserResDTO userResDTO = UserResDTO.builder()
				.id(me.getId())
				.username(me.getUsername())
				.email(me.getEmail())
				.role(me.getRole())
				.build();
		Response response = new Response(HttpStatus.OK, "registered", userResDTO);
		
		HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
}
