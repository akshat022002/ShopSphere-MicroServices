package com.akshat.coupon.coupon.service;



import com.akshat.coupon.common.enums.Role;
import com.akshat.coupon.coupon.dto.CouponCalculationRequest;
import com.akshat.coupon.coupon.dto.CouponResponse;
import com.akshat.coupon.coupon.dto.CreateCouponRequest;
import com.akshat.coupon.coupon.dto.UpdateCouponRequest;

import java.math.BigDecimal;

public interface CouponService {

    CouponResponse createCoupon(Long userId , Role role , CreateCouponRequest request,String token);

    CouponResponse getCouponById(Long couponId);

    CouponResponse updateCoupon(Long userId, Role role , Long couponId , UpdateCouponRequest request,String token);

    void deleteCoupon(Long userId, Role role , Long couponId,String token);

    BigDecimal calculateDiscount(String code, CouponCalculationRequest request);

    void useCoupon(Long userId, String code);
}
