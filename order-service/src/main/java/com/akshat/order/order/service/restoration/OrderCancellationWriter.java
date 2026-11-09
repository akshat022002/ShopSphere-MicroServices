package com.akshat.order.order.service.restoration;

import com.akshat.order.common.exception.ResourceNotFoundException;
import com.akshat.order.order.entity.Order;
import com.akshat.order.order.entity.OrderItem;
import com.akshat.order.order.entity.StockRestorationTask;
import com.akshat.order.order.enums.OrderStatus;
import com.akshat.order.order.enums.RestorationStatus;
import com.akshat.order.order.repository.OrderRepository;
import com.akshat.order.order.repository.StockRestorationTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class OrderCancellationWriter {

    private final OrderRepository orderRepository;
    private final StockRestorationTaskRepository taskRepository;

    public Order markCancelledAndCreateRestorationTasks(Long orderId,Long userId){

        Order order = orderRepository.findByIdAndUserId(orderId,userId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));

        if(order.getStatus()!= OrderStatus.CONFIRMED && order.getStatus()!=OrderStatus.PENDING){
            throw new IllegalStateException("Order cannot be cancelled during : "+order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);

        for(OrderItem item : order.getItems()){

            StockRestorationTask task = StockRestorationTask.builder()
                    .attemptCount(0)
                    .productId(item.getProductId())
                    .status(RestorationStatus.PENDING)
                    .orderId(order.getId())
                    .quantity(item.getQuantity())
                    .build();

            taskRepository.save(task);

        }

        return orderRepository.saveAndFlush(order);

    }

}
