package com.akshat.payment.payment.controller;

import com.akshat.payment.common.response.ApiResponse;
import com.akshat.payment.payment.dto.CreatePaymentResponse;
import com.akshat.payment.payment.dto.PaymentResponse;
import com.akshat.payment.payment.dto.VerifyPaymentRequest;
import com.akshat.payment.payment.service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Slf4j
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${internal.dashboard-service-secret}")
    private String dashboardServiceSecret;

    private final PaymentService paymentService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<CreatePaymentResponse>> createPayment(@PathVariable Long orderId,
                                                                            @RequestHeader("Authorization") String token) throws RazorpayException {

        CreatePaymentResponse response = paymentService.createPayment(orderId,token);

        ApiResponse<CreatePaymentResponse> apiResponse = new ApiResponse<>(
                true,
                "Payment created successfully",
                response,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(apiResponse);

    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(@RequestBody VerifyPaymentRequest request,
                                                                      @RequestHeader("Authorization") String token){

        PaymentResponse response = paymentService.verifyPayment(request, token);

        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>(
                true,
                "Payment verified successfully",
                response,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);

    }

    @GetMapping("/dashboard/admin")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenue(@RequestHeader("X-Service-Secret") String reqSecret){

        if(!dashboardServiceSecret.equals(reqSecret)){
            log.error("Secret Values are not identical");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        BigDecimal totalRevenue = paymentService.totalRevenue();

        ApiResponse<BigDecimal> apiResponse = new ApiResponse<>(
                true,
                "Total revenue fetched successfully",
                totalRevenue,
                LocalDateTime.now()
                );

        return ResponseEntity.ok(apiResponse);

    }

}
