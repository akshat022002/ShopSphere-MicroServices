package com.akshat.coupon.coupon.service;

import com.akshat.coupon.catalog.CatalogClient;
import com.akshat.coupon.catalog.dto.CatalogProduct;
import com.akshat.coupon.common.enums.Role;
import com.akshat.coupon.common.exception.AccessDeniedException;
import com.akshat.coupon.common.exception.DuplicateResourceException;
import com.akshat.coupon.common.exception.ResourceNotFoundException;
import com.akshat.coupon.coupon.dto.CouponCalculationRequest;
import com.akshat.coupon.coupon.dto.CouponResponse;
import com.akshat.coupon.coupon.dto.CreateCouponRequest;
import com.akshat.coupon.coupon.dto.UpdateCouponRequest;
import com.akshat.coupon.coupon.entity.Coupon;
import com.akshat.coupon.coupon.entity.CouponUsage;
import com.akshat.coupon.coupon.enums.DiscountType;
import com.akshat.coupon.coupon.mapper.CouponMapper;
import com.akshat.coupon.coupon.repository.CouponRepository;
import com.akshat.coupon.coupon.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService{

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final CatalogClient catalogClient;
    private final CouponMapper couponMapper;


    @Override
    public CouponResponse createCoupon(Long userId, Role role, CreateCouponRequest request, String token) {

        if(couponRepository.existsByCodeIgnoreCase(request.code())){
            throw new DuplicateResourceException("Coupon already exists");
        }

        Long productId = request.productId();

        if(productId!=null){

            CatalogProduct product = catalogClient.getProduct(productId,token);

            if(Role.ROLE_SELLER.equals(role) && !product.sellerId().equals(userId)){
                throw new AccessDeniedException("You are not authorized to create coupon for this product");
            }
        }

        if(Role.ROLE_SELLER.equals(role) && productId==null){
            throw new IllegalArgumentException("Seller must specify a product for coupon");
        }

        Coupon coupon = Coupon.builder()
                .discountType(request.discountType())
                .discountValue(request.discountValue())
                .expiresAt(request.expiresAt())
                .usageLimit(request.usageLimit())
                .productId(productId)
                .code(request.code().trim().toUpperCase())
                .maximumDiscount(request.maximumDiscount())
                .minimumOrderAmount(request.minimumOrderAmount())
                .build();

       Coupon savedCoupon =  couponRepository.save(coupon);

       return couponMapper.toResponse(savedCoupon);

    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(Long couponId) {

        return couponRepository.findById(couponId)
                .map(couponMapper::toResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Coupon not found with id : "+couponId));


    }

    @Override
    public CouponResponse updateCoupon(Long userId, Role role, Long couponId, UpdateCouponRequest request, String token) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(()-> new
                        ResourceNotFoundException("Coupon not found with id : "+couponId));

        CatalogProduct product = catalogClient.getProduct(coupon.getProductId(),token);

       validateCouponOwnership(userId,role,coupon,token);


        if (request.discountValue() != null) {
            coupon.setDiscountValue(request.discountValue());
        }

        if (request.minimumOrderAmount() != null) {
            coupon.setMinimumOrderAmount(request.minimumOrderAmount());
        }

        if (request.maximumDiscount() != null) {
            coupon.setMaximumDiscount(request.maximumDiscount());
        }

        if (request.expiresAt() != null) {
            coupon.setExpiresAt(request.expiresAt());
        }

        if (request.usageLimit() != null) {
            coupon.setUsageLimit(request.usageLimit());
        }

        if (request.active() != null) {
            coupon.setActive(request.active());
        }

        return couponMapper.toResponse(coupon);

    }

    @Override
    public void deleteCoupon(Long userId, Role role, Long couponId, String token) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(()-> new
                        ResourceNotFoundException("Coupon not found with id : "+couponId));

        CatalogProduct product = catalogClient.getProduct(coupon.getProductId(),token);

        validateCouponOwnership(userId,role,coupon,token);

        coupon.setActive(false);

    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal calculateDiscount(String code, CouponCalculationRequest request) {

        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(()-> new
                        ResourceNotFoundException("Coupon not found with code : "+code));

        BigDecimal eligibleAmount = calculateEligibleAmount(coupon,request);

        validateCoupon(coupon,eligibleAmount);

        BigDecimal discount;

        if(coupon.getDiscountType()==DiscountType.PERCENTAGE){
            discount= eligibleAmount
                    .multiply(coupon.getDiscountValue())
                    .divide(
                            BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP
                    );

            if(coupon.getMaximumDiscount()!=null){
                discount=discount.min(coupon.getMaximumDiscount());
            }
        } else {
            discount=coupon.getDiscountValue();
        }

        return discount.min(eligibleAmount);
    }

    @Override
    public void useCoupon(Long userId, String code) {

        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(()-> new ResourceNotFoundException("Coupon not found with code : "+code));

        if (!coupon.isActive()) {
            throw new IllegalStateException("Coupon is inactive");
        }

        if (LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
            throw new IllegalStateException("Coupon has expired");
        }

        if (coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new IllegalStateException("Coupon usage limit reached");
        }

        if (couponUsageRepository.existsByCouponIdAndUserId(
                coupon.getId(), userId)) {

            throw new IllegalStateException(
                    "Coupon has already been used by this customer");
        }

        CouponUsage couponUsage = CouponUsage.builder()
                .coupon(coupon)
                .userId(userId)
                .build();

        couponUsageRepository.save(couponUsage);

        coupon.setUsageCount(coupon.getUsageCount()+1);

    }

    //Helper function
    private void validateCoupon(Coupon coupon,BigDecimal eligibleAmount){

        if(!coupon.isActive()){
            throw new IllegalStateException("Coupon has expired");
        }

        if(LocalDateTime.now().isAfter(coupon.getExpiresAt())){
            throw new IllegalStateException("Coupon has expired");
        }

        if(coupon.getUsageCount() >= coupon.getUsageLimit()){
            throw new IllegalStateException("Coupon usage limit reached");
        }

        if(coupon.getMinimumOrderAmount()!=null &&
                eligibleAmount.compareTo(coupon.getMinimumOrderAmount())<0){
            throw new IllegalArgumentException("Minimum order amount is : "+coupon.getMinimumOrderAmount());
        }

        if(coupon.getDiscountType()== DiscountType.PERCENTAGE &&
                coupon.getDiscountValue().compareTo(BigDecimal.valueOf(75))>0){
            throw new IllegalArgumentException("Percentage discount cannot exceed 75%");
        }

        if(eligibleAmount.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Coupon is not applicable to this cart");
        }

    }

    //Helper function
    private void validateCouponOwnership(Long userId, Role role, Coupon coupon,String token){

        if(Role.ROLE_ADMIN.equals(role)){
            return;
        }

        Long productId = coupon.getProductId();

        if(productId==null || !catalogClient.isProductOwnedBySeller(productId,userId,token)){

            throw new AccessDeniedException("You are not authorized to manage this coupon");

        }

    }

    //Helper function
    private BigDecimal calculateEligibleAmount(Coupon coupon,
                                               CouponCalculationRequest request){




        if(coupon.getProductId()==null){

             return request.items()
                    .stream()
                    .map(item ->
                        item.price().multiply(BigDecimal.valueOf(item.quantity()))
                    )
                    .reduce(BigDecimal.ZERO,BigDecimal::add);

        }

        return request.items()
                .stream()
                .filter( item ->
                    item.productId().equals(coupon.getProductId())
                )
                .map(
                        item -> item.price().multiply(BigDecimal.valueOf(item.quantity()))
                )
                .reduce(BigDecimal.ZERO,BigDecimal::add);

    }
}
