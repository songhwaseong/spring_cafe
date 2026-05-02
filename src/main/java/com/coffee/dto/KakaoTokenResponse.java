package com.coffee.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// KakaoTokenResponse.java
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    // record 대신 일반 클래스 + getter 사용 (구버전 Java 호환)
    public String getAccessToken() {
        return accessToken;
    }
}
