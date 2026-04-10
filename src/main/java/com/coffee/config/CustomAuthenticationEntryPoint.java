package com.coffee.config;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String exception = (String) request.getAttribute("exception");
        int statusCode = HttpServletResponse.SC_UNAUTHORIZED;

        response.setContentType("application/json;charset=UTF-8");

        JSONObject responseJson = new JSONObject();
        try {
            if ("ExpiredJwtException".equals(exception)) {
                responseJson.put("code", "TOKEN_EXPIRED");
                responseJson.put("message", "토큰이 만료되었습니다.");
            } else  if ("SecurityException".equals(exception) | "MalformedJwtException".equals(exception) ) {
                responseJson.put("code", "SIGN_ERORR");
                responseJson.put("message", "토큰 서명/형식 오류");
            } else   if ("etcException".equals(exception)) {
                responseJson.put("code", "UNAUTHORIZED");
                responseJson.put("message", "기타 토큰 오류.");
            } else{
                responseJson.put("code", "UNAUTHORIZED");
                responseJson.put("message", "서버오류 오류.");
                statusCode =  HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
        }catch (JSONException je){
            System.out.println("json 오류");
        }
        response.setStatus(statusCode); // 401
        response.getWriter().print(responseJson);

    }
}
