package com.coffee.config;

import com.coffee.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import java.util.Date;
/*
메소드	역할
createToken	JWT 생성
getEmail	토큰에서 email 추출
validateToken	토큰 유효성 검사
*/
@Component
@RequiredArgsConstructor
public class JwtTokenProvider { // JWT 생성, 검증 기능 담당자 클래스

    @Value("${jwt.secret.key}")
    private String salt;

    private Key secretKey;

    private final CustomUserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email) { // 매개 변수 : 토큰 안에 사용자 식별값 저장
        // 만료 시간 1시간
        //long EXPIRATION = 3000;
        long EXPIRATION = 1000 * 60 * 60;
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmail(String token) { // JWT에서 사용자 정보 꺼내기

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

//    private Key getSigningKey() { // 위조 방지를 위한 서명
//
//        //String SECRET_KEY = "my-secret-key-my-secret-key-my-secret-key";
//        return Keys.hmacShaKeyFor(secretKey.getBytes());
//    }

    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getAccount(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에 담겨있는 유저 account 획득
    public String getAccount(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Authorization Header를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰 검증
//    public boolean validateToken(String token) {
//        try {
//            // Bearer 검증
//            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
//                return false;
//            } else {
//                token = token.split(" ")[1].trim();
//            }
//            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
//            // 만료되었을 시 false
//            return !claims.getBody().getExpiration().before(new Date());
//        } catch (Exception e) {
//            return false;
//        }
//    }

    public boolean validateToken(String token) {
        try {

            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            // 만료되었을 시 false
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            // 토큰 만료 예외 발생
            System.out.println("만료된 JWT 토큰입니다.1");
            throw e; // 필터로 던짐
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("잘못된 JWT 토큰입니다.2");
            throw e;
        }
    }
}