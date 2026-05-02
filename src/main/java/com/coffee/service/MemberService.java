package com.coffee.service;

import com.coffee.common.FuncData;
import com.coffee.constant.Role;
import com.coffee.dto.KakaoTokenResponse;
import com.coffee.dto.KakaoUserResponse;
import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate = new RestTemplate();


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
            throw new IOException("naver network Error");
        }
        return FuncData.parseStr(br.readLine(),"access_token");
    }

    public String getKkoAccessToken(String code) throws IOException {
        String clientId = "3c6d9d432fdac13ca5f9565cb91764c8";
        String clientSecret = "hZOlSVY3CFByezjdNKMA1Y9hnMxCG7VT";
        String redirect_uri = "http://localhost:5173/member/kauth";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        //params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirect_uri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        KakaoTokenResponse response = restTemplate.postForObject(
                "https://kauth.kakao.com/oauth/token",
                request,
                KakaoTokenResponse.class
        );

        return response.getAccessToken();
    }

    public Map<String, String> getTrackingInfo(String t_code, String t_invoice) throws IOException {
        String t_key = "6M1QKzCsuvgoVf5W1Y0gvQ";

        String apiURL = "https://info.sweettracker.co.kr/api/v1/trackingInfo?"
                + "t_code=" + t_code
                + "&t_invoice=" + t_invoice
                + "&t_key=" + t_key;

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET"); // GET 또는 POST

        int responseCode = con.getResponseCode();
        BufferedReader br;
        if(responseCode==200) { // 정상 호출
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {  // 에러 발생
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            throw new IOException("trackker network Error");
        }
        String data = br.readLine();
        System.out.println("data ::: "+data);
        return FuncData.parseInfo(data);
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

    public KakaoUserResponse getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                KakaoUserResponse.class
        ).getBody();
    }

}
