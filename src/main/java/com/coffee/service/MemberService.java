package com.coffee.service;

import com.coffee.common.FuncData;
import com.coffee.constant.Role;
import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service // 서비스 역할을 하며, 주로 로직 처리에 활용되는 자바 클래스입니다.
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository ;

    private final PasswordEncoder passwordEncoder ;


    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public void insert(Member bean) {
        // 사용자 '역할'과 '등록 일자'는 요기서 넣어 줍시다.
        bean.setRole(Role.USER);

        // 비밀 번호 암호화
        bean.setPassword(passwordEncoder.encode(bean.getPassword()));

        // 주의) Repository에서 인서트 작업은 save() 메소드를 사용합니다.
        memberRepository.save(bean);
    }

    public Optional<Member> findMemberById(Long memberId) {
        return this.memberRepository.findById(memberId);
    }

    public String getNavAccessToken(String code, String state) throws IOException {
        String clientId = "lkqzz1uWaGCn9UC_Xrpw";
        String clientSecret = "a4cS5FnAW2";

        String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&"
                + "client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&code=" + code
                + "&state=" + state;

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET"); // GET 또는 POST

        int responseCode = con.getResponseCode();
        BufferedReader br;
        if(responseCode==200) { // 정상 호출
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {  // 에러 발생
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
        return FuncData.parseStr(br.readLine(),"access_token");
    }

    public Map<String, String> getUserInfo(String accessToken) throws IOException {
        String apiUrl = "https://openapi.naver.com/v1/nid/me";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        conn.disconnect();
        return FuncData.parseInfo(response.toString());
    }

}
