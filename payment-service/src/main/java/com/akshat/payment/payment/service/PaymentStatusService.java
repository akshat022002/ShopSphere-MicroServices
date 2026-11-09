package com.akshat.payment.payment.service;

import com.akshat.payment.common.exception.ResourceNotFoundException;
import com.akshat.payment.payment.entity.Payment;
import com.akshat.payment.payment.enums.PaymentStatus;
import com.akshat.payment.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPaymentFailure(Long paymentId, String reason){

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Payment not found with id : "+paymentId));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);

    }

    public void markPaymentSuccess(Long paymentId,String razorpayPaymentId){

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Payment not found with id : "+paymentId));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setFailureReason(null);

    }
}
