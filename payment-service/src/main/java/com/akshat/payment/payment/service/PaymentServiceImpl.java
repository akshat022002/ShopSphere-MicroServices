package com.akshat.payment.payment.service;

import com.akshat.payment.common.exception.DuplicateResourceException;
import com.akshat.payment.common.exception.ResourceNotFoundException;
import com.akshat.payment.coupon.CouponClient;
import com.akshat.payment.order.OrderClient;
import com.akshat.payment.order.dto.OrderResponse;
import com.akshat.payment.payment.dto.CreatePaymentResponse;
import com.akshat.payment.payment.dto.PaymentResponse;
import com.akshat.payment.payment.dto.VerifyPaymentRequest;
import com.akshat.payment.payment.entity.Payment;
import com.akshat.payment.payment.enums.PaymentMethod;
import com.akshat.payment.payment.enums.PaymentStatus;
import com.akshat.payment.payment.repository.PaymentRepository;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@RequiredArgsConstructor
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final CouponClient couponClient;
    private final RazorpayService razorpayService;
    private final PaymentStatusService paymentStatusService;

    @Override
    public CreatePaymentResponse createPayment(Long orderId, String token) throws RazorpayException {

        OrderResponse order = orderClient.getOrder(orderId,token);

        if(paymentRepository.existsByOrderId(orderId)){
            throw new DuplicateResourceException("Payment already exists for order : "+orderId);
        }

        String receipt = "order"+orderId;

        String razorpayOrderId = razorpayService.createOrder(receipt,order.totalAmount());

        Payment payment = Payment.builder()
                .paymentMethod(PaymentMethod.RAZORPAY)
                .razorpayOrderId(razorpayOrderId)
                .amount(order.totalAmount())
                .status(PaymentStatus.CREATED)
                .orderId(order.id())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return new CreatePaymentResponse(savedPayment.getId(),
                savedPayment.getOrderId(),
                savedPayment.getAmount(),
                savedPayment.getPaymentMethod(),
                savedPayment.getStatus(),
                savedPayment.getRazorpayOrderId()
        );

    }

    @Override
    public PaymentResponse verifyPayment(VerifyPaymentRequest request, String token) {

        Payment payment = paymentRepository.findByRazorpayOrderId(request.razorpayOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for razorpay order : " + request.razorpayOrderId()));

        if(payment.getStatus()==PaymentStatus.SUCCESS){
            throw new DuplicateResourceException("Payment has already been verified");
        }

        OrderResponse order = orderClient.getOrder(payment.getOrderId(),token);

        boolean signatureValid = razorpayService.verifyPaymentSignature(
                request.razorpayOrderId(),
                request.razorpayPaymentId(),
                request.razorpaySignature()
        );

        if(!signatureValid){

            paymentStatusService.markPaymentFailure(payment.getId(), "Invalid razorpay payment signature");

            throw new IllegalArgumentException("Payment verification failed");
        }

        if(order.couponCode()!=null){
            couponClient.useCoupon(order.couponCode(),token);
        }

        paymentStatusService.markPaymentSuccess(payment.getId(), request.razorpayPaymentId());

        orderClient.confirmOrder(order.id());

        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );

    }

    @Override
    public BigDecimal totalRevenue() {
        return paymentRepository.getTotalRevenueByStatus(PaymentStatus.SUCCESS);
    }
}
