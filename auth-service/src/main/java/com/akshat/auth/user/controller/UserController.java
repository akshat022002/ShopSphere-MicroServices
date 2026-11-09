package com.akshat.auth.user.controller;

import com.akshat.auth.common.response.ApiResponse;
import com.akshat.auth.dashboard.dto.AdminUserStatistics;
import com.akshat.auth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Value("${internal.service-secret}")
    private String serviceSecret;

    private final UserService userService;

    @GetMapping("/dashboard/admin")
    public ResponseEntity<ApiResponse<AdminUserStatistics>> getUserCount(@RequestHeader("X-Service-Secret")
                                                                         String requestedSecret){

        if(!requestedSecret.equals(serviceSecret)){
            log.error("Secret value mismatch");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        AdminUserStatistics response = userService.getUserCount();

        ApiResponse<AdminUserStatistics> apiResponse = new ApiResponse<>(
                true,
                "User count fetched successfully",
                response,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
