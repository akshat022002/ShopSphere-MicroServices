package com.akshat.auth.auth.service;


import com.akshat.auth.auth.dto.AuthResponse;
import com.akshat.auth.auth.dto.LoginRequest;
import com.akshat.auth.auth.dto.RefreshTokenRequest;
import com.akshat.auth.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken,String refreshToken);

}
