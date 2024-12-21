package com.ii.controller;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ii.config.JwtFilter;
import com.ii.object.entity.User;
import com.ii.object.model.DTO.RegisterPostDTO;
import com.ii.object.model.DTO.LoginPostDTO;
import com.ii.object.model.DTO.RegisterResDTO;
import com.ii.object.model.DTO.LoginResDTO;
import com.ii.object.model.common.Response;
import com.ii.repository.IUserRepository;
import com.ii.service.AuthService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<Response> register(@RequestBody @Valid RegisterPostDTO registerPostDTO) {
		Response response;
		try {
			response = new Response(HttpStatus.OK, "registered", authService.register(registerPostDTO));
		} catch (MessagingException e) {
			response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "mail not served", null);
		}
		
		HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@GetMapping("/register/mail")
	public ResponseEntity<Response> mailAuth(@RequestParam(required = true, name = "authCode") UUID authCode) {
		boolean authed = authService.mailAuth(authCode);
		Response response;
		if(authed) response = new Response(HttpStatus.OK, "mail auth success", true);
		else response = new Response(HttpStatus.BAD_REQUEST, "mail auth failed", false);
		
		HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<Response> login(@RequestBody LoginPostDTO loginPostDTO) {
		
		String jwt = authService.login(loginPostDTO);
		
		Response response = new Response(HttpStatus.OK, "login success, jwt successfully provided", null);
		
		HttpHeaders headers= new HttpHeaders();
		headers.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer: " + jwt);
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
}
