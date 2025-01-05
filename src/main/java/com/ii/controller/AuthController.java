package com.ii.controller;

import java.security.SignatureException;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ii.object.model.DTO.FindPasswordReqDTO;
import com.ii.object.model.DTO.FindUsernameReqDTO;
import com.ii.object.model.DTO.LoginReqDTO;
import com.ii.object.model.DTO.RegisterReqDTO;
import com.ii.object.model.DTO.UpdateEmailReqDTO;
import com.ii.object.model.DTO.UpdatePasswordReqDTO;
import com.ii.object.model.common.Response;
import com.ii.service.AuthService;
import com.ii.utils.ResponseUtil;
import com.nimbusds.jose.util.Pair;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;

	// 회원가입
	@PostMapping("/register")
	public ResponseEntity<Response> register(@RequestBody @Valid RegisterReqDTO registerReqDTO) {
		try {
			return ResponseUtil.build(HttpStatus.OK, "registered", authService.register(registerReqDTO));
		} catch (MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 전송 실패!", null);
		} catch(DataIntegrityViolationException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
	}
	
	// 회원가입 시 이메일 인증
	@GetMapping("/email")
	public ResponseEntity<Response> mailAuth(@RequestParam(required = true, name="authCode") UUID authCode) {
		try {
			authService.mailAuth(authCode);
			return ResponseUtil.build(HttpStatus.OK, "메일 인증됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 구성 실패!", null);
		} catch(BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
	}
	
	// 로그인
	@PostMapping("/login")
	public ResponseEntity<Response> login(@RequestBody @Valid LoginReqDTO loginReqDto) {
		
		Pair<String, String> tokenPair = authService.login(loginReqDto);
		
		return ResponseUtil.build(HttpStatus.OK, "login success, jwt successfully provided", null, tokenPair.getLeft(), tokenPair.getRight());
	}
	
	// Refresh Token을 이용한 Access & Refresh Token 재발급
	@PostMapping("/refresh")
	public ResponseEntity<Response> refresh(HttpServletRequest request) {
		
		try {
			Pair<String, String> tokenPair = authService.refresh(request);
			return ResponseUtil.build(HttpStatus.OK, "refresh success, jwt successfully provided", null, tokenPair.getLeft(), tokenPair.getRight());
		} catch (BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		} catch (SignatureException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "Access Token Error!", null);
		}
		
	}
	
	// 로그아웃
	@GetMapping("/logout")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> logout(HttpServletRequest request) {
		
		try {
			authService.logout(request);
			return ResponseUtil.build(HttpStatus.OK, "Log out complete", null, null, null);
			// Access & Refresh Token 값을 null로 주어서 클라이언트의 Authorization Header와 Cookie 삭제
		} catch (BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
		
	}
	
	// 회원탈퇴
	@DeleteMapping("/unregister")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> unregister(HttpServletRequest request) {
		
		try {
			authService.unregister(request);
			return ResponseUtil.build(HttpStatus.NO_CONTENT, "유저 삭제됨 ㅅㄱ ㅂ2", null);
		} catch (BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "요청이 뭔가 이상하네요...", null);
		}
		
	}
	
	// 이메일 변경
	@PutMapping("/email")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> updateEmail(@RequestBody @Valid UpdateEmailReqDTO updateEmailReqDTO) throws BadRequestException {
		
		try {
			authService.updateEmail(updateEmailReqDTO);
			return ResponseUtil.build(HttpStatus.NO_CONTENT, "메일 변경됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 구성 실패!", null);
		} catch(IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "요청이 뭔가 이상하네요...", null);
		} catch (BadCredentialsException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "비밀번호 불일치", null);
		} catch(DataIntegrityViolationException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
		
	}
	
	// 비밀번호 변경
	@PostMapping("/password/update")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> updatePassword(@RequestBody @Valid UpdatePasswordReqDTO udpatePasswordReqDTO) throws BadRequestException {
		
		try {
			authService.updatePassword(udpatePasswordReqDTO);
			return ResponseUtil.build(HttpStatus.NO_CONTENT, "메일 변경됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 구성 실패!", null);
		} catch(IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "요청이 뭔가 이상하네요...", null);
		} catch (BadCredentialsException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "비밀번호 불일치", null);
		} catch (BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
		
	}
	
	// 비밀번호 변경 시 메일 인증
	@GetMapping("/password/update")
	public ResponseEntity<Response> passwordMailAuth(@RequestParam(required = true, name="authCode") UUID authCode) {
		try {
			authService.passwordMailAuth(authCode);
			return ResponseUtil.build(HttpStatus.OK, "메일 인증됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 구성 실패!", null);
		} catch(BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "요청이 뭔가 이상하네요...", null);
		}
	}
	
	// 유저네임 찾기
	@PostMapping("/username/find")
	public ResponseEntity<Response> findUsername(@RequestBody @Valid FindUsernameReqDTO findUsernameReqDTO) {
		
		try {
			authService.findUsername(findUsernameReqDTO);
			return ResponseUtil.build(HttpStatus.OK, "메일로 사용자 이름 전송됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 구성 실패!", null);
		} catch(BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
		
	}
	
	// 비밀번호 찾기
	@PostMapping("/password/find")
	public ResponseEntity<Response> findPassword(@RequestBody @Valid FindPasswordReqDTO findPasswordReqDTO) {
		
		try {
			authService.findPassword(findPasswordReqDTO);
			return ResponseUtil.build(HttpStatus.OK, "메일로 비밀번호 전송됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 구성 실패!", null);
		} catch(BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		} catch(DataIntegrityViolationException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
		
	}
	
}
