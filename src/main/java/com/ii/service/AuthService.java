package com.ii.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ii.config.TokenProvider;
import com.ii.object.entity.MailAuth;
import com.ii.object.entity.PasswordHistory;
import com.ii.object.entity.User;
import com.ii.object.model.DTO.LoginPostDTO;
import com.ii.object.model.DTO.LoginResDTO;
import com.ii.object.model.DTO.RegisterPostDTO;
import com.ii.object.model.DTO.RegisterResDTO;
import com.ii.repository.IMailAuthRepository;
import com.ii.repository.IPasswordHistoryRepository;
import com.ii.repository.IUserRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	
	private final IUserRepository userRepository;
	private final IMailAuthRepository mailAuthRepository;
	private final IPasswordHistoryRepository passwordHistoryRepository;
	
	private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    
    private final MailService mailService;
	
    @Transactional
	public RegisterResDTO register(RegisterPostDTO registerPostDTO) throws MessagingException{
		
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(registerPostDTO.getPassword());
        
		User user = User.builder()
				.username(registerPostDTO.getUsername())
				.email(registerPostDTO.getEmail())
				.hashedPassword(hashedPassword)
				.build();
		User createdUser = userRepository.save(user);
		
		MailAuth mailAuth = MailAuth.builder()
				.user(createdUser)
				.build();
		mailAuthRepository.save(mailAuth);
		
		PasswordHistory passwordHistory = PasswordHistory.builder()
				.user(createdUser)
				.hashedPassword(hashedPassword)
				.build();
		passwordHistoryRepository.save(passwordHistory);
		
		mailService.sendMail(createdUser.getEmail(), mailAuth.getAuthCode());
		
		RegisterResDTO registerResDTO = new RegisterResDTO(createdUser.getId(), createdUser.getUsername());
		return registerResDTO;
	}
	
    @Transactional
	public boolean mailAuth(UUID authCode) {
		LocalDateTime now = LocalDateTime.now();
		MailAuth mailAuth = mailAuthRepository.findByAuthCode(authCode);
		if(mailAuth == null) {
			System.out.println("뭔가 이상하노");
		}
		if(Duration.between(now, mailAuth.getCreatedAt()).getSeconds() > 1800) { // 30분 넘어서 시도
			mailAuth.setAuthCode(UUID.randomUUID());
			mailAuthRepository.save(mailAuth); // 새로운 인증 UUID 생성
			return false;
		}
		User authUser = mailAuth.getUser();
		authUser.setMailAuth(true);
		userRepository.save(authUser);
		mailAuthRepository.delete(mailAuth);
		return true;		
	}
	
	public String login(LoginPostDTO loginPostDTO){
		
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(loginPostDTO.getUsername(), loginPostDTO.getPassword());
		
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.createToken(authentication);
		
		return jwt;
	}

}
