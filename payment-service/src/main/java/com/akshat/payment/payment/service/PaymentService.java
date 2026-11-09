package com.akshat.payment.payment.service;

import com.akshat.payment.payment.dto.CreatePaymentResponse;
import com.akshat.payment.payment.dto.PaymentResponse;
import com.akshat.payment.payment.dto.VerifyPaymentRequest;
import com.akshat.payment.payment.enums.PaymentStatus;
import com.razorpay.RazorpayException;

import java.math.BigDecimal;

public interface PaymentService {

    CreatePaymentResponse createPayment(Long orderId, String token) throws RazorpayException;

    PaymentResponse verifyPayment(VerifyPaymentRequest request, String token);

    BigDecimal totalRevenue();
}