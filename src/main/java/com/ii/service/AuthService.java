package com.ii.service;

import java.security.SignatureException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ii.config.security.TokenProvider;
import com.ii.object.entity.MailAuth;
import com.ii.object.entity.PasswordAuth;
import com.ii.object.entity.PasswordHistory;
import com.ii.object.entity.RefreshToken;
import com.ii.object.entity.User;
import com.ii.object.model.DTO.FindPasswordReqDTO;
import com.ii.object.model.DTO.FindUsernameReqDTO;
import com.ii.object.model.DTO.LoginReqDTO;
import com.ii.object.model.DTO.RegisterReqDTO;
import com.ii.object.model.DTO.RegisterResDTO;
import com.ii.object.model.DTO.UpdateEmailReqDTO;
import com.ii.object.model.DTO.UpdatePasswordReqDTO;
import com.ii.repository.IMailAuthRepository;
import com.ii.repository.IPasswordAuthRepository;
import com.ii.repository.IPasswordHistoryRepository;
import com.ii.repository.IRefreshTokenRepository;
import com.ii.repository.IUserRepository;
import com.ii.utils.SecurityUtil;
import com.nimbusds.jose.util.Pair;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	
	private final IUserRepository userRepository;
	private final IMailAuthRepository mailAuthRepository;
	private final IPasswordHistoryRepository passwordHistoryRepository;
	private final IRefreshTokenRepository refreshTokenRepository;
	private final IPasswordAuthRepository passwordAuthRepository;
	
    private final MailService mailService;
    
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
	
    @Transactional
	public RegisterResDTO register(RegisterReqDTO registerReqDTO) throws MessagingException{
		
        String hashedPassword = passwordEncoder.encode(registerReqDTO.getPassword());
        
		User user = User.builder()
				.username(registerReqDTO.getUsername())
				.email(registerReqDTO.getEmail())
				.nickname(registerReqDTO.getNickname())
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
		
		RefreshToken refreshToken = RefreshToken.builder()
				.user(createdUser)
				.build();
		refreshTokenRepository.save(refreshToken);
		
		mailService.sendAuthMail(createdUser.getEmail(), mailAuth.getAuthCode());
		
		RegisterResDTO registerResDTO = new RegisterResDTO(createdUser.getId(), createdUser.getUsername());
		return registerResDTO;
	}
	
    @Transactional
	public boolean mailAuth(UUID authCode) throws BadRequestException, MessagingException {
		LocalDateTime now = LocalDateTime.now();
		MailAuth mailAuth = mailAuthRepository.findByAuthCode(authCode);
		if(mailAuth == null) {
			throw new BadRequestException("잘못된 인증코드");
		}
		if(Duration.between(now, mailAuth.getCreatedAt()).getSeconds() > 1800) { // 30분 넘어서 시도
			mailAuth.setAuthCode(UUID.randomUUID());
			mailAuthRepository.save(mailAuth); // 새로운 인증 UUID 생성
			mailService.sendPasswordUpdateMail(null, authCode);
			return false;
		}
		User authUser = mailAuth.getUser();
		authUser.setMailAuth(true);
		userRepository.save(authUser);
		mailAuthRepository.delete(mailAuth);
		return true;		
	}
	
	public Pair<String, String> login(LoginReqDTO loginReqDTO){
		
		User user = userRepository.findByUsername(loginReqDTO.getUsername());
		if(user == null) throw new UsernameNotFoundException("그런 사용자 없음");
		if(!user.getMailAuth()) throw new BadCredentialsException("메일 인증 좀");
		if(!passwordEncoder.matches(loginReqDTO.getPassword(), user.getHashedPassword())) throw new BadCredentialsException("비밀번호 틀림 ㅅㄱ");
		
		String accessTokenString = tokenProvider.generateAccessToken(user.getUsername(), user.getRoles());
		UsernamePasswordAuthenticationToken accessAuthenticationToken = tokenProvider.getAuthentication(accessTokenString);
		// authenticationToken 검증, 검증 성공시 role이 추가된 authentication 반환
		SecurityContextHolder.getContext().setAuthentication(accessAuthenticationToken);
		
		String refreshTokenString = tokenProvider.generateRefreshToken(user.getUsername());
		
		RefreshToken refreshToken = refreshTokenRepository.getByuserUsername(user.getUsername());
		refreshToken.setToken(refreshTokenString);
		refreshTokenRepository.save(refreshToken);
		
		return Pair.of(accessTokenString, refreshTokenString);
	}
	
	public Pair<String, String> refresh(HttpServletRequest request) throws BadRequestException, SignatureException, ExpiredJwtException {
		
		String oldAccessTokenString = SecurityUtil.resolveToken(request);
		if(!StringUtils.hasText(oldAccessTokenString)) throw new BadRequestException("no access token");
		try {
			UsernamePasswordAuthenticationToken oldAccessAuthenticationToken = tokenProvider.getAuthentication(oldAccessTokenString);
		} catch(ExpiredJwtException e) {
			
		}
		
		
		String refreshTokenString = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie:cookies){
            if (cookie.getName().equals("Refresh")) refreshTokenString = cookie.getValue();
        }
		if(refreshTokenString == null) throw new BadRequestException("empty refresh token");
		if(tokenProvider.isExpired(refreshTokenString)) throw new BadRequestException("expired refresh token");
		
		if(!refreshTokenRepository.existsByToken(refreshTokenString)) throw new BadRequestException("invalid refresh token");
		
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = tokenProvider.getAuthentication(refreshTokenString);
		String username = usernamePasswordAuthenticationToken.getName();
		User user = userRepository.findByUsername(username);
		
		String accessTokenString = tokenProvider.generateAccessToken(user.getUsername(), user.getRoles());
		UsernamePasswordAuthenticationToken accessAuthenticationToken = tokenProvider.getAuthentication(accessTokenString);
		SecurityContextHolder.getContext().setAuthentication(accessAuthenticationToken);
		
		String newRefreshTokenString = tokenProvider.generateRefreshToken(user.getUsername());
		
		RefreshToken refreshToken = refreshTokenRepository.getByToken(refreshTokenString);
		refreshToken.setToken(newRefreshTokenString);
		refreshTokenRepository.save(refreshToken);
		
		return Pair.of(accessTokenString, newRefreshTokenString);
		
	}
	
	@Transactional
	public void unregister(HttpServletRequest request) throws BadRequestException {
		String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new BadRequestException("로그인 정보 없음"));
		userRepository.deleteByUsername(username);
	}
	
	@Transactional
	public void updateEmail(UpdateEmailReqDTO updateEmailReqDTO) throws BadRequestException, BadCredentialsException, MessagingException {
		String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new BadRequestException("로그인 정보 없음"));
		User me = userRepository.findByUsername(username);
		if(!passwordEncoder.matches(updateEmailReqDTO.getPassword(), me.getHashedPassword())) {
			throw new BadCredentialsException("비밀번호 불일치");
		}
		me.setEmail(updateEmailReqDTO.getEmail());
		me.setMailAuth(false);
		MailAuth mailAuth = MailAuth.builder()
				.user(me)
				.build();
		mailAuthRepository.save(mailAuth);
		userRepository.save(me);
		
		mailService.sendAuthMail(me.getEmail(), mailAuth.getAuthCode());
	}
	
	@Transactional
	public void updatePassword(UpdatePasswordReqDTO updatePasswordReqDTO) throws BadRequestException, BadCredentialsException, MessagingException {
		String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new BadRequestException("로그인 정보 없음"));
		User me = userRepository.findByUsername(username);
		if(!passwordEncoder.matches(updatePasswordReqDTO.getOldPassword(), me.getHashedPassword())) {
			throw new BadCredentialsException("비밀번호 불일치");
		}
		PasswordHistory passwordHistory = passwordHistoryRepository.findByUser(me);
		if(passwordHistory == null) throw new BadRequestException("비밀번호 기록이 왜 없노?");
		if(passwordEncoder.matches(updatePasswordReqDTO.getNewPassword(), passwordHistory.getHashedPassword())) {
			throw new BadRequestException("임마는 비밀번호를 같은걸로 변경 할라하노");
		}
		PasswordAuth passwordAuth = PasswordAuth.builder()
				.user(me)
				.newHashedPassword(passwordEncoder.encode(updatePasswordReqDTO.getNewPassword()))
				.build();
		passwordAuthRepository.save(passwordAuth);
		
		mailService.sendPasswordUpdateMail(me.getEmail(), passwordAuth.getAuthCode());
	}
	
	@Transactional
	public void passwordMailAuth(UUID authCode) throws BadRequestException, MessagingException {
		
		LocalDateTime now = LocalDateTime.now();
		PasswordAuth passwordAuth = passwordAuthRepository.findByAuthCode(authCode);
		if(passwordAuth == null) {
			throw new BadRequestException("잘못된 인증코드");
		}
		User me = passwordAuth.getUser();
		if(Duration.between(now, passwordAuth.getCreatedAt()).getSeconds() > 1800) { // 30분 넘어서 시도
			passwordAuth.setAuthCode(UUID.randomUUID());
			passwordAuthRepository.save(passwordAuth); // 새로운 인증 UUID 생성
			mailService.sendAuthMail(me.getEmail(), passwordAuth.getAuthCode());
			return;
		}
		PasswordHistory passwordHistory = passwordHistoryRepository.findByUser(me);
		passwordHistory.setHashedPassword(passwordAuth.getNewHashedPassword());
		passwordHistoryRepository.save(passwordHistory);
		me.setHashedPassword(passwordAuth.getNewHashedPassword());
		userRepository.save(me);
		passwordAuthRepository.delete(passwordAuth);
		return;		
	}
	
	public void findUsername(FindUsernameReqDTO findUsernameReqDTO) throws BadRequestException, MessagingException {
		
		String email = findUsernameReqDTO.getEmail();
		User me = userRepository.findByEmail(email);
		if(me == null) {
			throw new BadRequestException("해당 email을 사용하는 사용자를 찾을 수 없습니다.");
		}
		
		mailService.sendUsernameMail(me.getEmail(), me.getUsername());
	}
	
	@Transactional
	public void findPassword(FindPasswordReqDTO findPasswordReqDTO) throws BadRequestException, MessagingException {
		
		String username = findPasswordReqDTO.getUsername();
		User me = userRepository.findByUsername(username);
		if(me == null) {
			throw new BadRequestException("해당 username을 사용하는 사용자를 찾을 수 없습니다.");
		}
		if(!me.getMailAuth()) {
			throw new BadRequestException("님아 메일 인증부터 좀 하셈.");
		}
		String newPassword = SecurityUtil.generateSecureString();
		String newHashedPassword = passwordEncoder.encode(newPassword);
		me.setHashedPassword(newHashedPassword);
		userRepository.save(me);
		
		mailService.sendPasswordFindMail(me.getEmail(), newPassword);
	}

}
