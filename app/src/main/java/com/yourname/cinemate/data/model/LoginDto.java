package com.yourname.cinemate.data.model;

public class LoginDto {
    private String email;
    private String password;
    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}