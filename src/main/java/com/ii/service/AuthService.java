package com.ii.service;

import java.security.SignatureException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
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
	private final IRefreshTokenRepository refreshTokenRepository;
	private final IPasswordAuthRepository passwordAuthRepository;
	
    private final MailService mailService;
    
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    
	// 회원가입
    @Transactional
	public RegisterResDTO register(RegisterReqDTO registerReqDTO) throws MessagingException{
		
        String hashedPassword = passwordEncoder.encode(registerReqDTO.getPassword());	// 패스워드 해시
        
		User user = User.builder()
				.username(registerReqDTO.getUsername())
				.email(registerReqDTO.getEmail())
				.nickname(registerReqDTO.getNickname())
				.hashedPassword(hashedPassword)
				.build();	// 유저 생성
		
		MailAuth mailAuth = MailAuth.builder()
				.user(user)
				.build();	// 메일인증 정보 생성 후 유저 관계 주입
		
		PasswordHistory passwordHistory = PasswordHistory.builder()
				.user(user)
				.hashedPassword(hashedPassword)
				.build();	// 패스워드 기록 생성 후 유저 관계 주입
		
//		RefreshToken refreshToken = RefreshToken.builder()
//				.user(user)
//				.build();	// Refresh Token 정보 생성 후 유저 관계 주입
		
		user.setMailAuth(mailAuth);	// 상호 관계 설정
//		user.setRefreshToken(refreshToken);
		user.setPasswordHistory(passwordHistory);
		
		try {
			userRepository.save(user);	// 유저 정보 및 관련 객체 저장
			mailService.sendAuthMail(user.getEmail(), mailAuth.getAuthCode());	// 객체 저장 예외 발생하지 않으면 인증 메일 전송
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException("", e.getMostSpecificCause());	// 영속화 예외 (Unique Key Constraint 등...)
		}

		RegisterResDTO registerResDTO = new RegisterResDTO(user.getId(), user.getUsername());
		return registerResDTO;
	}
	
    // 회원가입 시 메일인증
    @Transactional
	public void mailAuth(UUID authCode) throws BadRequestException, MessagingException {
		LocalDateTime now = LocalDateTime.now();
		MailAuth mailAuth = mailAuthRepository.findByAuthCode(authCode);	// 인증코드가 실제로 존재하는지 확인
		if(mailAuth == null) {
			throw new BadRequestException("잘못된 인증코드");	// 없으면 예외 처리
		}
		if(Duration.between(now, mailAuth.getCreatedAt()).getSeconds() > 1800) { // 유효기간 만료 시 새로운 인증코드와 함께 인증메일 재전송
			mailAuth.setAuthCode(UUID.randomUUID());
			mailAuthRepository.save(mailAuth); // 새로운 인증 UUID 생성
			mailService.sendPasswordUpdateMail(null, authCode);
			throw new BadRequestException("인증코드 만료! 재전송...");
		}
		User authUser = mailAuth.getUser();
		authUser.setIsMailAuthed(true);		// 사용자 메일인증 완료 처리
		userRepository.save(authUser);
		mailAuthRepository.delete(mailAuth);	// 필요 없어진 메일 인증 정보 삭제
		return;
	}
	
    // 로그인
	public Pair<String, String> login(LoginReqDTO loginReqDTO){
		
		User user = userRepository.findByUsername(loginReqDTO.getUsername());	// username으로 유저 탐색
		if(user == null) throw new UsernameNotFoundException("그런 사용자 없음");	// 사용자 없으면 예외 발생
		if(!user.getIsMailAuthed()) throw new BadCredentialsException("메일 인증 좀");	// 메일 인증 미완료 시 로그인 불가
		if(!passwordEncoder.matches(loginReqDTO.getPassword(), user.getHashedPassword())) throw new BadCredentialsException("비밀번호 틀림 ㅅㄱ");
		
		UUID deviceId = UUID.randomUUID();
		
		String accessTokenString = tokenProvider.generateAccessToken(user.getUsername(), user.getRoles(), deviceId);	// username과 권한(=Role)로 Access Token 생성
		UsernamePasswordAuthenticationToken accessAuthenticationToken = tokenProvider.getAuthentication(accessTokenString);
		// authenticationToken 검증, 검증 성공시 role이 추가된 authentication 반환
		SecurityContextHolder.getContext().setAuthentication(accessAuthenticationToken);
		//SecurityContext에 인증정보 저장
		
		String refreshTokenString = tokenProvider.generateRefreshToken(user.getUsername(), deviceId);	// Refresh Token 생성
		
		RefreshToken refreshToken = RefreshToken.builder()
				.user(user)
				.token(refreshTokenString)
				.deviceId(deviceId)
				.build();
		
		refreshTokenRepository.save(refreshToken);	// Refresh Token 갱신 및 저장
		
		return Pair.of(accessTokenString, refreshTokenString);	// Access Token, Refresh Token의 쌍 반환
	}
	
	// Refresh Token을 이용하여 Access Token 재발급
	public Pair<String, String> refresh(HttpServletRequest request) throws BadRequestException, SignatureException, ExpiredJwtException {
		
		
		String refreshTokenString = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie:cookies){
            if (cookie.getName().equals("Refresh")) refreshTokenString = cookie.getValue();	// 쿠키에 저장된 Refresh Token을 가져옴
        }
		if(refreshTokenString == null) throw new BadRequestException("empty refresh token"); // Refresh Token이 없을 경우 예외 발생
		if(tokenProvider.isExpired(refreshTokenString)) {
			refreshTokenRepository.deleteByToken(refreshTokenString);
			throw new BadRequestException("expired refresh token"); // Refresh Token이 만료된 경우 Refresh Token 저장소에서 삭제하고 예외 발생
		}
		
		RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString);
		if(refreshToken == null) throw new BadRequestException("invalid refresh token");
		 // Refresh Token이 저장되어 관리되고 있는 토큰이 아니라면 예외 발생
		
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = tokenProvider.getAuthentication(refreshTokenString);
		// Refresh Token에서 인증 정보 가져옴
		UUID refreshDeviceId = tokenProvider.getDeviceId(refreshTokenString);	// refresh token에 저장된 device id를 가져옴
		
		User user = refreshToken.getUser();
		
		String accessTokenString = tokenProvider.generateAccessToken(user.getUsername(), user.getRoles(), refreshDeviceId);	// 조회된 user 정보를 기반으로 AccessToken 재발급
		UsernamePasswordAuthenticationToken accessAuthenticationToken = tokenProvider.getAuthentication(accessTokenString);
		SecurityContextHolder.getContext().setAuthentication(accessAuthenticationToken);	// 갱신된 인증정보 저장
		
		String newRefreshTokenString = tokenProvider.generateRefreshToken(user.getUsername(), refreshDeviceId);	// 조회된 user 정보를 기반으로 RefreshToken 재발급
		
		refreshToken.setToken(newRefreshTokenString);
		refreshTokenRepository.save(refreshToken);	// 갱신된 Refresh Token db에 저장
		
		return Pair.of(accessTokenString, newRefreshTokenString);	// Access Token, Refresh Token의 쌍 반환
	}
	
	// 로그아웃
	@Transactional
	public void logout(HttpServletRequest request) throws BadRequestException {
		String accessTokenString = SecurityUtil.resolveToken(request);
		UUID deviceId = tokenProvider.getDeviceId(accessTokenString);
		RefreshToken refreshToken = refreshTokenRepository.findByDeviceId(deviceId);	// access token의 device id를 통해 Refresh Token 식별
		refreshToken.setToken("");
		refreshTokenRepository.save(refreshToken);	// 현재 Refresh Token 무효 처리
	}
	
	// 회원탈퇴
	@Transactional
	public void unregister(HttpServletRequest request) throws BadRequestException {
		String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new BadRequestException("로그인 정보 없음"));
		// 로그인 되지 않은 사용자면 예외 발생
		userRepository.deleteByUsername(username);	// 유저 삭제
	}
	
	// 이메일 변경
	@Transactional
	public void updateEmail(UpdateEmailReqDTO updateEmailReqDTO) throws BadRequestException, BadCredentialsException, MessagingException {
		String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new BadRequestException("로그인 정보 없음"));
		// 로그인 되지 않은 사용자면 예외 발생
		User me = userRepository.findByUsername(username);
		if(!passwordEncoder.matches(updateEmailReqDTO.getPassword(), me.getHashedPassword())) {
			throw new BadCredentialsException("비밀번호 불일치");	// 비밀번호 불일치 예외
		}
		me.setEmail(updateEmailReqDTO.getEmail());	// 메일 주소 변경
		me.setIsMailAuthed(false);					// 메일 인증 무효화
		MailAuth mailAuth = MailAuth.builder()
				.user(me)
				.build();	// 메일 인증 정보 생성
		me.setMailAuth(mailAuth);
		
		try {
			userRepository.save(me);	// 유저 정보 및 관련 객체 저장
			mailService.sendAuthMail(me.getEmail(), mailAuth.getAuthCode());	// 객체 저장 예외 발생하지 않으면 인증 메일 전송
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException("", e.getMostSpecificCause());	// 영속화 예외 (Unique Key Constraint 등...)
		}		
		
	}
	
	// 비밀번호 변경
	@Transactional
	public void updatePassword(UpdatePasswordReqDTO updatePasswordReqDTO) throws BadRequestException, BadCredentialsException, MessagingException {
		String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new BadRequestException("로그인 정보 없음"));
		User me = userRepository.findByUsername(username);
		if(!passwordEncoder.matches(updatePasswordReqDTO.getOldPassword(), me.getHashedPassword())) {
			throw new BadCredentialsException("비밀번호 불일치");
		}
		PasswordHistory passwordHistory = me.getPasswordHistory();
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
	
	// 비밀번호 변경 메일인증
	@Transactional
	public void passwordMailAuth(UUID authCode) throws BadRequestException, MessagingException {
		
		LocalDateTime now = LocalDateTime.now();
		PasswordAuth passwordAuth = passwordAuthRepository.findByAuthCode(authCode);	// 인증정보 존재하는지 인증코드로 조회
		if(passwordAuth == null) {
			throw new BadRequestException("잘못된 인증코드");	// 인증정보 없으므로 예외
		}
		User me = passwordAuth.getUser();
		if(Duration.between(now, passwordAuth.getCreatedAt()).getSeconds() > 1800) { // 만료된 인증코드일 경우 인증코드 재생성, 메일 재송신
			passwordAuth.setAuthCode(UUID.randomUUID());
			passwordAuthRepository.save(passwordAuth); // 새로운 인증 UUID 생성
			mailService.sendAuthMail(me.getEmail(), passwordAuth.getAuthCode());
			return;
		}
		PasswordHistory passwordHistory = me.getPasswordHistory();
		passwordHistory.setHashedPassword(passwordAuth.getNewHashedPassword());	// 비밀번호 기록 갱신
		me.setHashedPassword(passwordAuth.getNewHashedPassword());	// 비밀번호 변경
		userRepository.save(me);	// 변경 내용 영속화
		passwordAuthRepository.delete(passwordAuth);	// 필요 없어진 변경 인증 정보 삭제
		return;		
	}
	
	// 유저네임 찾기
	public void findUsername(FindUsernameReqDTO findUsernameReqDTO) throws BadRequestException, MessagingException {
		
		String email = findUsernameReqDTO.getEmail();	// 가입할 때 사용한 이메일
		User me = userRepository.findByEmail(email);	// 이메일로 유저 조회ㄴ
		if(me == null) {
			throw new BadRequestException("해당 email을 사용하는 사용자를 찾을 수 없습니다.");	// 해당 이메일로 가입한 사용자 없을 경우 예외 발생
		}
		
		mailService.sendUsernameMail(me.getEmail(), me.getUsername());	// 메일로 유저네임 송신
	}

	// 비밀번호 찾기
	@Transactional
	public void findPassword(FindPasswordReqDTO findPasswordReqDTO) throws BadRequestException, MessagingException {
		
		String username = findPasswordReqDTO.getUsername();	// 비밀번호 찾으려는 사용자 유저네임
		User me = userRepository.findByUsername(username);	// 유저네임으로 사용자 조회
		if(me == null) {
			throw new BadRequestException("해당 username을 사용하는 사용자를 찾을 수 없습니다.");	// 사용자 없을 경우 예외 발생
		}
		if(!me.getIsMailAuthed()) {
			throw new BadRequestException("님아 메일 인증부터 좀 하셈.");	// 메일 인증 안된 경우 예외 발생
		}
		String newPassword = SecurityUtil.generateSecureString();	// 새로운 비밀번호 (랜덤) 생성
		String newHashedPassword = passwordEncoder.encode(newPassword);
		me.setHashedPassword(newHashedPassword);
		
		try {
			userRepository.save(me);	// 새로운 비밀번호 저장, 이때 비밀번호 기록에는 남기지 않음.
			mailService.sendPasswordFindMail(me.getEmail(), newPassword);	// 메일로 새로운 비밀번호 송신
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException("", e.getMostSpecificCause());	// 영속화 예외 (Unique Key Constraint 등...)
		}	
		
	}

}
