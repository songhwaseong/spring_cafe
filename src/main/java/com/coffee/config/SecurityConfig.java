package com.coffee.config;

import com.coffee.handler.CustomLoginFailureHandler;
import com.coffee.handler.CustomLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

/*
    1. /images/** 경로는 로그인 안 해도 접근 허용
    2. 이미지 요청을 제외한 나머지 모든 요청은 로그인이 필요합니다.
       /login 페이지는 직접 구현 요망
*/
        // 인증 없이 요청을 허용할 url 배열
        String[] permitUrls = {
                "/images/**", "/fruit/**", "/css/**", "/js/**", "/member/signup", "/member/login", "/api/**"
        };

        // Spring Security 기본 정책 : POST / PUT / DELETE 요청은 CSRF 토큰 필요
        // 지금은 CSRF을 일단 비활성화 시켜 두고, 이후 JWT를 사용하면 지금 겪는 문제(CSRF + 리다이렉트)는 깔끔하게 해결됩니다.
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permitUrls).permitAll() // 이미지 허용
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/member/login") // React에서 로그인시 요청할 url
                        .usernameParameter("email") // 로그인시 id 역할을 할 컬럼명
                        .passwordParameter("password") // 비밀 번호 컬럼명
                        .permitAll() // 누구든지 접근 허용
                        .successHandler(handler()) // 로그인 성공시 수행할 동작을 여기에 명시
                        .failureHandler(failureHandler()) // 로그인 실패시
                );


        http.cors(cors -> {});

        return http.build();
    }

    @Bean // 개발자가 정의한 "로그인 성공 핸들러" 객체
    public CustomLoginSuccessHandler handler(){
        return new CustomLoginSuccessHandler();
    }

    @Bean // 개발자가 정의한 "로그인 성공 핸들러" 객체
    public CustomLoginFailureHandler failureHandler(){
        return new CustomLoginFailureHandler();
    }

}