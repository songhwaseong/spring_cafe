package com.coffee.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// KakaoUserResponse.java
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserResponse(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            String email,
            Profile profile
    ) {}

    public record Profile(
            String nickname
    ) {}
}