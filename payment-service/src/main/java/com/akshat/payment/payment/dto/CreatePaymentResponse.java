package com.akshat.payment.payment.dto;

import com.akshat.payment.payment.enums.PaymentMethod;
import com.akshat.payment.payment.enums.PaymentStatus;

import java.math.BigDecimal;

public record CreatePaymentResponse(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String razorpayOrderId
) {
}
