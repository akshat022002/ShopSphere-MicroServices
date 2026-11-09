package com.akshat.notification.service;

import com.akshat.notification.dto.OrderEvent;
import com.akshat.notification.enums.OrderEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;

    public void processOrderEvent(OrderEvent event){
        if(event.eventType()== OrderEventType.ORDER_PLACED){
            handleOrderPlaced(event);
        }

        if(event.eventType()==OrderEventType.ORDER_CANCELLED){
            handleOrderCancelled(event);
        }
    }

    public void handleOrderPlaced(OrderEvent event){

        String subject = "ShopSphere - Order Confirmation";

        String body = """
                Hello,
                
                Your order has been placed successfully.
                
                Order Id : %d
                
                Thank you for shopping with ShopSphere.
                """.formatted(event.orderId());


        emailService.sendEmail(event.customerEmail(),subject,body);
        log.info("Order confirmation processed for orderId={}", event.orderId());

    }

    public void handleOrderCancelled(OrderEvent event){

        String subject = "ShopSphere - Order Cancelled";

        String body = """
                Hello,
                
                Your order has been cancelled.
                
                Order Id : %d
                
                If you have any questions ,please contact ShopSphere support.
                """.formatted(event.orderId());

        emailService.sendEmail(event.customerEmail(),subject,body);
        log.info("Order cancellation processed for orderId={}",event.orderId());

    }

}


