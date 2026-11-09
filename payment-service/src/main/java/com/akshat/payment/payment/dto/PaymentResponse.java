package com.akshat.payment.payment.dto;

import com.akshat.payment.payment.enums.PaymentMethod;
import com.akshat.payment.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        Long paymentId,
        Long orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String razorpayOrderId,
        String razorpayPaymentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
