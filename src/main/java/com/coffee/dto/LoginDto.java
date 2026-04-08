package com.coffee.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginDto {
    private String email;
    private String password;
}
