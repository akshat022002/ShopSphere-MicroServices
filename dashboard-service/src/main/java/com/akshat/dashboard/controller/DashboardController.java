package com.akshat.dashboard.controller;

import com.akshat.dashboard.common.response.ApiResponse;
import com.akshat.dashboard.dto.AdminDashboardResponse;
import com.akshat.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard(){

        AdminDashboardResponse response = dashboardService.getAdminDashboard();

        ApiResponse<AdminDashboardResponse> apiResponse = new ApiResponse<>(
                true,
                "Admin dashboard fetched successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

}
