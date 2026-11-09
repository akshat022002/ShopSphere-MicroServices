package com.akshat.payment.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class RazorpayServiceImpl implements RazorpayService{

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Override
    public String createOrder(String receipt, BigDecimal amount)  {

        try{
            RazorpayClient razorpayClient = new RazorpayClient(keyId,keySecret);

            JSONObject orderRequest = new JSONObject();

            long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).longValueExact();

            orderRequest.put("receipt",receipt);
            orderRequest.put("amount",amountInPaise);
            orderRequest.put("currency","INR");

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            log.info("Razorpay order created successfully : {}",razorpayOrder.get("id").toString());

            return razorpayOrder.get("id");

        } catch (RazorpayException e){

            log.error("Failed to create razorpay order for receipt : {} ",receipt,e);

            throw new RuntimeException("Unable to create razorpay order");

        }

    }

    @Override
    public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {

        try{
            JSONObject attributes = new JSONObject();

            attributes.put("razorpay_order_id",razorpayOrderId);
            attributes.put("razorpay_payment_id",razorpayPaymentId);
            attributes.put("razorpay_signature",razorpaySignature);

            return Utils.verifyPaymentSignature(attributes,keySecret);

        } catch(RazorpayException e){

            log.error("Razorpay payment signature verification failed ",e);
            return false;
        }

    }
}
