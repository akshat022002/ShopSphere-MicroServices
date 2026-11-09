package com.akshat.auth.auth.service;


import com.akshat.auth.auth.dto.AuthResponse;
import com.akshat.auth.auth.dto.LoginRequest;
import com.akshat.auth.auth.dto.RefreshTokenRequest;
import com.akshat.auth.auth.dto.RegisterRequest;
import com.akshat.auth.common.exception.BadRequestException;
import com.akshat.auth.common.exception.DuplicateResourceException;
import com.akshat.auth.security.jwt.JwtService;
import com.akshat.auth.security.redis.TokenBlacklistService;
import com.akshat.auth.security.service.CustomUserDetailsService;
import com.akshat.auth.security.userdetails.UserDetailsImplementation;
import com.akshat.auth.user.entity.User;
import com.akshat.auth.user.enums.AuthProvider;
import com.akshat.auth.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        log.info("Register request received for email : {}", request.email());

        String email = request.email().trim().toLowerCase(Locale.ROOT);

      if(userRepository.existsByEmail(email)){
          throw new DuplicateResourceException("Email already registered");
      }

      User user = User.builder()
              .name(request.name())
              .email(email)
              .password(passwordEncoder.encode(request.password()))
              .role(request.role())
              .authProvider(AuthProvider.LOCAL)
              .enabled(true)
              .build();

      userRepository.save(user);

      String accessToken = jwtService
        .generateAccessToken(new
                UserDetailsImplementation(user));

      String refreshToken = jwtService
              .generateRefreshToken(new
                   UserDetailsImplementation(user));

        log.info("User registered successfully : {}", user.getEmail());

      return new AuthResponse(accessToken,refreshToken);

    }

    @Override
    public AuthResponse login(LoginRequest request) {

        log.info("Login request received for : {}",request.email());

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,request.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UserDetailsImplementation userDetailsImpl = (UserDetailsImplementation) userDetails;

        String accessToken = jwtService.generateAccessToken(userDetailsImpl);

        String refreshToken = jwtService.generateRefreshToken(userDetailsImpl);

        log.info("User logged in successfully : {}", email);

        return new AuthResponse(accessToken,refreshToken);
    }


    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.refreshToken();

        if(tokenBlacklistService.isBlacklisted(refreshToken)){
            throw new BadRequestException("Refresh token has been revoked");
        }

        try{

            if(!jwtService.isRefreshToken(refreshToken)){
                throw new BadRequestException("Invalid refresh token type");
            }

            String email = jwtService.extractUsername(refreshToken).trim().toLowerCase(Locale.ROOT);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UserDetailsImplementation userDetailsImpl = (UserDetailsImplementation) userDetails;

            if(!jwtService.isTokenValid(refreshToken,userDetailsImpl)){
                throw new BadRequestException("Invalid/Expired refresh token");
            }

            String accessToken = jwtService.generateAccessToken(userDetailsImpl);
            String newRefreshToken = jwtService.generateRefreshToken(userDetailsImpl);

            return new AuthResponse(accessToken,newRefreshToken);

        } catch (JwtException | IllegalArgumentException e){
            throw new BadRequestException("Invalid/Expired refresh token");
        }

    }

    @Override
    public void logout(String accessToken,String refreshToken) {

        log.info("Logout request received");
        tokenBlacklistService.blacklistToken(accessToken);
        tokenBlacklistService.blacklistToken(refreshToken);
        log.info("Token blacklisted successfully");

    }
}
