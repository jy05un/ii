package com.ii.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ii.service.CustomOAuth2UserSerivce;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final TokenProvider tokenProvider;
    
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserSerivce customOAuth2UserSerivce;
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    	return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    String loginUrl() throws Exception {
    	return "/auth/login";
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()) // session 아니고 jwt라 disable, ㅎ

                .exceptionHandling(exceptionHandling -> exceptionHandling
                		.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                		.accessDeniedHandler(jwtAccessDeniedHandler)
                		)

                .headers(headers -> headers
                		.frameOptions(frameOptions -> frameOptions.sameOrigin()))

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                		.anyRequest().permitAll()
                ) // Role 체크는 @PreAuthorize 데코레이터를 통해 이루어질 것이므로 anyRequest에 대해 permit 한다              
                
                .oauth2Login(
                		oauth2Login -> oauth2Login.userInfoEndpoint(
                				userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserSerivce)
                				)
                		.successHandler(oAuth2SuccessHandler)
                		.failureHandler(oAuth2FailureHandler)
                		)
                
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                ;
        		// JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig class 적용

        return httpSecurity.build();
    }

}