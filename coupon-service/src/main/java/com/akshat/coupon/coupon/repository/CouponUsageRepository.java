package com.akshat.coupon.coupon.repository;


import com.akshat.coupon.coupon.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage,Long> {

    boolean existsByCouponIdAndUserId(Long couponId,Long userId);

}
