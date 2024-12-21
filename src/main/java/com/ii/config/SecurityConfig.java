package com.ii.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()) // session 아니고 jwt라 disable

                .exceptionHandling(exceptionHandling -> exceptionHandling
                		.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                		.accessDeniedHandler(jwtAccessDeniedHandler)
                		)

                // enable h2-console
                .headers(headers -> headers
                		.frameOptions(frameOptions -> frameOptions.sameOrigin()))

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                		.requestMatchers("/auth/register").permitAll() // 회원가입 api
                        .requestMatchers("/auth/register/mail").permitAll() // 메일 인증 api
                        .requestMatchers("/auth/login").permitAll() // 로그인 api
                        .anyRequest().authenticated() // 그 외 인증 없이 접근X
                        ) // HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정하겠다.
                
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        		// JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig class 적용

        return httpSecurity.build();
    }

}