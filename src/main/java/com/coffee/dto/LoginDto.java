package com.coffee.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
    private boolean autoLogin;
}
