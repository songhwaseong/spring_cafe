package com.coffee.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String exception = (String) request.getAttribute("exception");

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401


        JSONObject responseJson = new JSONObject();
        try {
            System.out.println("exception :  "+exception);
            if ("ExpiredToken".equals(exception)) {
                responseJson.put("code", "TOKEN_EXPIRED");
                responseJson.put("message", "토큰이 만료되었습니다.");
            } else {
                responseJson.put("code", "UNAUTHORIZED");
                responseJson.put("message", "인증되지 않은 사용자입니다.");
            }
        }catch (JSONException je){
            System.out.println("json 오류");
        }

        response.getWriter().print(responseJson);

    }
}
