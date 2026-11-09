package com.akshat.coupon.coupon.mapper;

import com.akshat.coupon.coupon.dto.CouponResponse;
import com.akshat.coupon.coupon.entity.Coupon;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    public CouponResponse toResponse(Coupon coupon);

}