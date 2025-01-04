package com.ii.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ii.service.CustomOAuth2UserSerivce;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	/**
	 * 보안 설정을 총괄하는 클래스
	 */

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final TokenProvider tokenProvider;
    
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserSerivce customOAuth2UserSerivce;
    
    // 배포 시에 사용할 웹서버의 주소
    @Value("${app.web-uri}")
    private String webUrl;
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();	// 전역에서 사용할 패스워드 인코더를 Bean으로 설정함
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()) // session 아니고 jwt라 disable, ㅎ

                .exceptionHandling(exceptionHandling -> exceptionHandling	// jwt 검증 중 발생한 Exception에 대한 핸들러 설정
                		.authenticationEntryPoint(jwtAuthenticationEntryPoint)	// Authentication Exception에 대한 핸들러	(=로그인 안함)
                		.accessDeniedHandler(jwtAccessDeniedHandler)	// AccessDeny Exception에 대한 핸들러 (=권한 없음)
                		)

                .headers(headers -> headers											// 외부 도메인에서 iFrame으로 표시됨을 허용할지의 여부
                		.frameOptions(frameOptions -> frameOptions.sameOrigin()))	// domain이 같을 때에만 허용

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                		.anyRequest().permitAll()
                ) // Role 체크는 @PreAuthorize 데코레이터를 통해 이루어질 것이므로 anyRequest에 대해 permit 한다              
                
                .oauth2Login(
                		oauth2Login -> oauth2Login.userInfoEndpoint(	// oauth2 로그인 시, 리다이렉트 되어 넘어온 데이터를 어떻게 처리할 것인가에 대한 엔드포인트
                				userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserSerivce)	// customOAuth2UserService에서 넘어온 데이터를 처리함
                				)
                		.successHandler(oAuth2SuccessHandler)	// 이때, 로그인 성공 시 oAuth2SuccessHandler에서 로그인 성공 처리를 함
                		.failureHandler(oAuth2FailureHandler)	// 로그인 실패 시 oAuth2FailureHandler에서 로그인 실패에 대한 처리를 함
                		)
                
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                // JWT 필터를 기본 인증 필터인 UsernamePasswordAuthenticationFilter 앞에 배치함
                .formLogin(formLogin -> formLogin.disable())	// 기본 인증방식(=form login)을 비활성화하여 기본 인증필터(=UsernamePasswordAuthenticationFilter)를 제거함
                ;

        return httpSecurity.build();
    }
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {	// CORS 설정
        CorsConfiguration configuration = new CorsConfiguration();
        // 해당 도메인으로부터의 요청만을 허용함
        configuration.setAllowedOriginPatterns(Arrays.asList(webUrl, "http://localhost:3000/", "http://test.hlemont.xyz/", "http://test.hlemont.xyz:3000/", "http://144.24.79.146:3000/", "http://test.hlemont.xyz:80"));
        // 해당 Method의 요청만을 허용함
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("X-AUTH-ACCESS-TOKEN"));
        // 자격증명(Authorization 헤더, Cookie)을 담아 보내는 것을 허용함
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);	// 모든 요청에 대해 위 설정대로 허용함
        return source;
    }

}