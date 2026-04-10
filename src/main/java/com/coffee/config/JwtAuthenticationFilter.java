package com.coffee.config;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override // 모든 요청이 들어올 때 컨트롤러 보다 먼저 실행이 되는 핵심 로직
    protected void doFilterInternal(
            HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain) throws ServletException, IOException {
/*
doFilterInternal 메소드가 하는 역할
    요청 헤더에서 Authorization 값을 가져온다
    값이 존재하고 "Bearer "로 시작하는지 확인한다
    "Bearer "를 제거하여 JWT 토큰만 추출한다
    토큰의 유효성을 검증한다 (validateToken)
    토큰에서 사용자 이메일을 추출한다 (getEmail)
    토큰의 claims에서 role 값을 추출한다
    role을 기반으로 권한 객체(GrantedAuthority)를 생성한다
    이메일 + 권한 정보를 이용해 Authentication 객체를 생성한다
    생성한 Authentication 객체를 SecurityContext에 저장한다
    다음 필터 또는 컨트롤러로 요청을 전달한다 (filterChain.doFilter)


* */


        // 클라이언트 요청 예시 : [Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...]의 형식
        String bearer = request.getHeader("Authorization");


        if (bearer != null && bearer.startsWith("Bearer ")) {  // "Bearer "로 시작하는지 검증
            String token = bearer.substring("Bearer ".length()); // "Bearer " (7글자) 제거

            if (jwtTokenProvider.validateToken(token, request)) {
                String email = jwtTokenProvider.getEmail(token);
                Claims claims = jwtTokenProvider.getClaims(token); // payload 전체 정보
                String role = claims.get("role", String.class); // 역할(Role) 추출

                // Spring Security는 권한을 "ROLE_" prefix로 관리합니다.
                //예: USER → ROLE_USER
                // 이 권한은 인증 + 인가(Authorization)에 사용됩니다.
                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + role));

                // Authentication 객체 생성
                /* 값	의미
principal	email (사용자 식별자)
credentials	null (비밀번호는 JWT에서는 필요 없음)
authorities	권한 목록 */
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}