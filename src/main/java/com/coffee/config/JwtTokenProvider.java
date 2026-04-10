package com.coffee.config;

import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/*
메소드	역할
createToken	JWT 생성
getEmail	토큰에서 email 추출
validateToken	토큰 유효성 검사
*/
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider { // JWT 생성, 검증 기능 담당자 클래스
    private final MemberRepository memberRepository ;
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key signingKey;

    @PostConstruct
    protected void init() {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private Key getSigningKey() {
        return signingKey;
    }

    public String createToken(Member member) { // 매개 변수 : 토큰 안에 사용자 식별값 저장
        // 만료 시간 1시간
        return Jwts.builder()
                .setSubject(member.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.getSigningKey(), SignatureAlgorithm.HS256)
                .claim("role", member.getRole().name())
                //.setClaims(Map.of("role", member.getRole().name()))
                .compact(); // 최종 문자열 생성
    }

    public String getEmail(String token) { // JWT에서 사용자 정보 꺼내기
        return this.getClaims(token).getSubject();
    }

//    private Key getSigningKey() { // 위조 방지를 위한 서명
//        // 이 값도 사실 노출 방지를 위하여 어딘가에 숨겨야 함
//        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    Jws<Claims> claims = null;
    public boolean validateToken(String token, HttpServletRequest req) { // JWT 토큰 유효성 검사
        try {

            claims = Jwts.parserBuilder()
                    .setSigningKey(this.getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            log.info("==================> {}", token);
            log.info("==================> {}", claims);
            log.info("==================> {}", getClaims(token));
            log.info("==================> {}", getClaims(token).getExpiration());
            log.info("==================> {}", getClaims(token).getSubject());
            log.info("==================> {}", getClaims(token).getNotBefore());
            log.info("==================> {}", getClaims(token).getAudience());
            log.info("==================> {}", getClaims(token).getIssuedAt());
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 만료됨");
            req.setAttribute("exception", "ExpiredJwtException");
            throw new ExpiredJwtException(null, null, "토큰 만료됨");
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("토큰 서명/형식 오류");
            req.setAttribute("exception", "SecurityException");
            throw e;
        } catch (Exception e) {
            System.out.println("기타 토큰 오류");
            req.setAttribute("exception", "etcException");
            throw e;
        }finally {
            req.setAttribute("statusCode", 401);
        }
    }
}