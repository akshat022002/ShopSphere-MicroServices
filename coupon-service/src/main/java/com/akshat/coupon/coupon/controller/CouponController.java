package com.akshat.coupon.coupon.controller;

import com.akshat.coupon.common.enums.Role;
import com.akshat.coupon.common.response.ApiResponse;
import com.akshat.coupon.coupon.dto.CouponCalculationRequest;
import com.akshat.coupon.coupon.dto.CouponResponse;
import com.akshat.coupon.coupon.dto.CreateCouponRequest;
import com.akshat.coupon.coupon.dto.UpdateCouponRequest;
import com.akshat.coupon.coupon.service.CouponService;
import com.akshat.coupon.security.userdetails.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CreateCouponRequest request,
                                                                    Authentication authentication,
                                                                    @RequestHeader("Authorization")String token){

        Long userId = currentUserProvider.getUserId(authentication);
        Role role = currentUserProvider.getUserRole(authentication);

        CouponResponse response = couponService.createCoupon(userId, role,request,token);

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>(true,
                                         "Coupon created successfully",
                                                   response,
                                                   LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{couponId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER','CUSTOMER')")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(@PathVariable Long couponId){

        CouponResponse response = couponService.getCouponById(couponId);

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>(true,
                "Coupon fetched successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{couponId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(@PathVariable Long couponId,
                                                                    @RequestBody @Valid UpdateCouponRequest request,
                                                                    Authentication authentication,
                                                                    @RequestHeader("Authorization")String token){

        Long userId = currentUserProvider.getUserId(authentication);
        Role role = currentUserProvider.getUserRole(authentication);

        CouponResponse response = couponService.updateCoupon(userId,role,couponId,request,token);

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>(true,
                "Coupon updated successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<CouponResponse>> deleteCoupon(@PathVariable Long couponId,
                                                                    Authentication authentication,
                                                                    @RequestHeader("Authorization")String token){

        Long userId = currentUserProvider.getUserId(authentication);
        Role role = currentUserProvider.getUserRole(authentication);

        couponService.deleteCoupon(userId,role,couponId,token);

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>(true,
                "Coupon soft deleted/deactivated successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateDiscount(@RequestParam String code,
                                                                     @RequestBody CouponCalculationRequest request,
                                                                     @RequestHeader("Authorization")String token
                                                                    ){


        BigDecimal discount = couponService.calculateDiscount(code,request);

        ApiResponse<BigDecimal> apiResponse = new ApiResponse<>(true,
                "Discount calculated successfully",
                discount,
                LocalDateTime.now());

        return  ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{code}/use")
    public ResponseEntity<ApiResponse<Void>> useCoupon(@PathVariable String code,Authentication authentication){

            Long userId = currentUserProvider.getUserId(authentication);

            couponService.useCoupon(userId,code);

            ApiResponse<Void> response = new ApiResponse<>(true,
                    "Coupon used successfully",
                    null,LocalDateTime.now());

            return ResponseEntity.ok(response);

    }

}
