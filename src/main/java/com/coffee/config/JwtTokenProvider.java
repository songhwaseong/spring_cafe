package com.coffee.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;

import java.util.Date;
/*
메소드	역할
createToken	JWT 생성
getEmail	토큰에서 email 추출
validateToken	토큰 유효성 검사
*/
@Component
public class JwtTokenProvider { // JWT 생성, 검증 기능 담당자 클래스

    public String createToken(String email) { // 매개 변수 : 토큰 안에 사용자 식별값 저장
        // 만료 시간 1시간
        long EXPIRATION = 1000 * 60 * 60;
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmail(String token) { // JWT에서 사용자 정보 꺼내기

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    private Key getSigningKey() { // 위조 방지를 위한 서명

        String SECRET_KEY = "my-secret-key-my-secret-key-my-secret-key";
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public boolean validateToken(String token) { // JWT 유효성 검사
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}