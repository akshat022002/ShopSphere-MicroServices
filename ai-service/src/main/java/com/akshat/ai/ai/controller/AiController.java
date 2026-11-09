package com.akshat.ai.ai.controller;

import com.akshat.ai.common.response.ApiResponse;
import com.akshat.ai.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','SELLER')")
public class AiController {

    private final AiService aiService;

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<String>> ask(@RequestParam String question){

        String response =  aiService.ask(question);

        ApiResponse<String> apiResponse = new ApiResponse<>(
                true,
                "Response Generated Successfully",
                response,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);

    }

}
