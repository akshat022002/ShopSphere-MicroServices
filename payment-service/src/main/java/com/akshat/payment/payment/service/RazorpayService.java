package com.akshat.payment.payment.service;

import com.razorpay.RazorpayException;

import java.math.BigDecimal;

public interface RazorpayService {

    String createOrder(String receipt, BigDecimal amount) throws RazorpayException;

    boolean verifyPaymentSignature(String razorpayOrderId,
                                   String razorpayPaymentId,
                                   String razorpaySignature);

}


