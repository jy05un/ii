package com.ii.controller;

import java.nio.charset.Charset;
import java.security.SignatureException;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.ii.utils.SecurityUtil;
import com.nimbusds.jose.util.Pair;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<Response> register(@RequestBody @Valid RegisterReqDTO registerReqDTO) {
		Response response;
		try {
			response = new Response(HttpStatus.OK, "registered", authService.register(registerReqDTO));
		} catch (MessagingException e) {
			response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "mail not served", null);
		}
		
		HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@GetMapping("/email")
	public ResponseEntity<Response> mailAuth(@RequestParam(required = true, name="authCode") UUID authCode) {
		try {
			authService.mailAuth(authCode);
			return ResponseUtil.build(HttpStatus.OK, "메일 인증됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 재전송 실패!", null);
		} catch(BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "요청이 뭔가 이상하네요...", null);
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<Response> login(@RequestBody @Valid LoginReqDTO loginReqDto) {
		
		Pair<String, String> tokenPair = authService.login(loginReqDto);
		
		Response response = new Response(HttpStatus.OK, "login success, jwt successfully provided", null);
		
		HttpHeaders headers= new HttpHeaders();
		headers.add(SecurityUtil.AUTHORIZATION_HEADER, "Bearer: " + tokenPair.getLeft());
		headers.add(HttpHeaders.SET_COOKIE, SecurityUtil.parseRefreshCookie(tokenPair.getRight()));
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<Response> refresh(HttpServletRequest request) {
		
		try {
			Pair<String, String> tokenPair = authService.refresh(request);
			Response response = new Response(HttpStatus.OK, "refresh success, jwt successfully provided", null);
			
			HttpHeaders headers= new HttpHeaders();
			headers.add(SecurityUtil.AUTHORIZATION_HEADER, "Bearer: " + tokenPair.getLeft());
			headers.add(HttpHeaders.SET_COOKIE, SecurityUtil.parseRefreshCookie(tokenPair.getRight()));
	        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
	        return new ResponseEntity<>(response, headers, HttpStatus.OK);
		} catch (BadRequestException e) {
			Response response = new Response(HttpStatus.BAD_REQUEST, "bad request!", null);
			HttpHeaders headers= new HttpHeaders();
	        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
	        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
		} catch (SignatureException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "Access Token Error!", null);
		}
		
	}
	
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
	
	@PutMapping("/email")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> updateEmail(@RequestBody @Valid UpdateEmailReqDTO updateEmailReqDTO) throws BadRequestException {
		
		try {
			authService.updateEmail(updateEmailReqDTO);
			return ResponseUtil.build(HttpStatus.NO_CONTENT, "메일 변경됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 전송 실패!", null);
		} catch(IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "요청이 뭔가 이상하네요...", null);
		} catch (BadCredentialsException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "비밀번호 불일치", null);
		}
		
	}
	
	@PostMapping("/password/update")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> updatePassword(@RequestBody @Valid UpdatePasswordReqDTO udpatePasswordReqDTO) throws BadRequestException {
		
		try {
			authService.updatePassword(udpatePasswordReqDTO);
			return ResponseUtil.build(HttpStatus.NO_CONTENT, "메일 변경됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 전송 실패!", null);
		} catch(IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "요청이 뭔가 이상하네요...", null);
		} catch (BadCredentialsException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "비밀번호 불일치", null);
		} catch (BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
		
	}
	
	@GetMapping("/password/update")
	public ResponseEntity<Response> passwordMailAuth(@RequestParam(required = true, name="authCode") UUID authCode) {
		try {
			authService.passwordMailAuth(authCode);
			return ResponseUtil.build(HttpStatus.OK, "메일 인증됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 재전송 실패!", null);
		} catch(BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "요청이 뭔가 이상하네요...", null);
		}
	}
	
	@PostMapping("/username/find")
	public ResponseEntity<Response> findUsername(@RequestBody @Valid FindUsernameReqDTO findUsernameReqDTO) {
		
		try {
			authService.findUsername(findUsernameReqDTO);
			return ResponseUtil.build(HttpStatus.OK, "메일로 사용자 이름 전송됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 재전송 실패!", null);
		} catch(BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
		
	}
	
	@PostMapping("/password/find")
	public ResponseEntity<Response> findPassword(@RequestBody @Valid FindPasswordReqDTO findPasswordReqDTO) {
		
		try {
			authService.findPassword(findPasswordReqDTO);
			return ResponseUtil.build(HttpStatus.OK, "메일로 비밀번호 전송됨", null);
		} catch(MessagingException e) {
			return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 재전송 실패!", null);
		} catch(BadRequestException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
		
	}
	
}
