package com.akshat.order.order.service;

import com.akshat.order.cart.CartClient;
import com.akshat.order.cart.dto.CartItemResponse;
import com.akshat.order.cart.dto.CartResponse;
import com.akshat.order.catalog.CatalogClient;
import com.akshat.order.catalog.dto.CatalogProduct;
import com.akshat.order.common.exception.BadRequestException;
import com.akshat.order.common.exception.ResourceNotFoundException;
import com.akshat.order.coupon.CouponClient;
import com.akshat.order.coupon.dto.CouponCalculationRequest;
import com.akshat.order.inventory.InventoryClient;
import com.akshat.order.order.dto.AdminOrderStatistics;
import com.akshat.order.order.dto.OrderEvent;
import com.akshat.order.order.dto.OrderResponse;
import com.akshat.order.order.dto.OrderSummaryResponse;
import com.akshat.order.order.entity.Order;
import com.akshat.order.order.entity.OrderItem;
import com.akshat.order.order.entity.StockRestorationTask;
import com.akshat.order.order.enums.OrderEventType;
import com.akshat.order.order.enums.OrderStatus;
import com.akshat.order.order.enums.RestorationStatus;
import com.akshat.order.order.mapper.OrderMapper;
import com.akshat.order.order.repository.OrderItemRepository;
import com.akshat.order.order.repository.OrderRepository;
import com.akshat.order.order.repository.StockRestorationTaskRepository;
import com.akshat.order.order.service.restoration.OrderCancellationWriter;
import com.akshat.order.order.service.restoration.StockRestorationService;
import com.akshat.order.order.service.restoration.StockRestorationTaskWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CatalogClient catalogClient;
    private final CartClient cartClient;
    private final InventoryClient inventoryClient;
    private final CouponClient couponClient;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final OrderCancellationWriter orderCancellationWriter;
    private final StockRestorationTaskRepository stockRestorationTaskRepository;
    private final StockRestorationService stockRestorationService;
    private final StockRestorationTaskWriter stockRestorationTaskWriter;


    @Override
    public OrderResponse placeOrder(Long userId, String couponCode, String token,String email) {

        CartResponse cart = cartClient.getCart(token);

        if(cart.items().isEmpty()){
            throw new IllegalArgumentException("Cannot place order with empty cart");
        }

        BigDecimal subtotal = cart.items()
                .stream()
                .map(item-> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal discount =BigDecimal.ZERO;

        if(couponCode!=null && !couponCode.isBlank()){

            CouponCalculationRequest request = new CouponCalculationRequest(
                    cart.items().stream()
                            .map(item -> new CouponCalculationRequest.Item(
                                                        item.productId(),
                                                        item.price(),
                                                        item.quantity()
                                    )
                            ).toList()
            );

            discount = couponClient.calculateDiscount(couponCode,request,token);

        }

        BigDecimal totalAmount = subtotal.subtract(discount);

        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .userId(userId)
                .totalAmount(totalAmount)
                .subtotal(subtotal)
                .discount(discount)
                .items(new ArrayList<>())
                .couponCode( couponCode != null && !couponCode.isBlank()
                        ? couponCode.trim().toUpperCase()
                        : null)
                .build();

        Order savedOrder = orderRepository.saveAndFlush(order);
        Long orderId = savedOrder.getId();

        List<CartItemResponse> reservedItems = new ArrayList<>();

        try {
            for (var cartItem : cart.items()) {

                CatalogProduct product = catalogClient.getProduct(cartItem.productId(), token);

                inventoryClient.decreaseStock(cartItem.productId(), cartItem.quantity(),orderId);

                reservedItems.add(cartItem);

                BigDecimal price = cartItem.price();

                BigDecimal itemSubtotal = cartItem.price().multiply(BigDecimal.valueOf(cartItem.quantity()));

                OrderItem orderItem = OrderItem.builder()
                        .productId(cartItem.productId())
                        .price(price)
                        .subtotal(itemSubtotal)
                        .order(savedOrder)
                        .productName(product.name())
                        .quantity(cartItem.quantity())
                        .build();

                savedOrder.getItems().add(orderItem);
            }
        } catch (RuntimeException ex) {

            for (CartItemResponse reservedItem : reservedItems) {
                try {

                    log.info("Stock preserved");
                    inventoryClient.increaseStock(reservedItem.productId(), reservedItem.quantity(),orderId);

                } catch (RuntimeException compensationEx) {

                    ex.addSuppressed(compensationEx);

                    try{
                        stockRestorationTaskWriter.createTask(
                                orderId, reservedItem.productId(),reservedItem.quantity(),compensationEx);

                    } catch (RuntimeException taskEx){
                        ex.addSuppressed(taskEx);

                        log.error("Failed to create restoration task for orderId={}, productId={}",
                                orderId,
                                reservedItem.productId(),
                                taskEx);
                    }

                }
            }

            throw ex;
        }

        savedOrder = orderRepository.save(savedOrder);

        OrderEvent event = new OrderEvent(OrderEventType.ORDER_PLACED,
                savedOrder.getId(),
                savedOrder.getUserId(),
                email
                );

        applicationEventPublisher.publishEvent(event);

        cartClient.clearCart(token);

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getOrderById(Long userId, Long orderId) {

        return orderRepository.findByIdAndUserId(orderId,userId)
                .map(orderMapper::toResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));


    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderSummaryResponse> getMyOrders(Long userId, Pageable pageable) {

        return orderRepository.findAllByUserId(userId,pageable)
                .map(order -> new OrderSummaryResponse(
                        order.getId(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getCreatedAt()
                        ));
    }

    @Override
    public OrderResponse cancelOrder(Long userId, Long orderId,String email) {

        Order order = orderCancellationWriter.markCancelledAndCreateRestorationTasks(orderId,userId);

        List<StockRestorationTask> tasks = stockRestorationTaskRepository
                .findByOrderIdAndStatus(order.getId(), RestorationStatus.PENDING);


        for(StockRestorationTask task : tasks){

           stockRestorationService.processTask(task);

        }

        OrderEvent event = new OrderEvent(
                OrderEventType.ORDER_CANCELLED,
                order.getId(),
                order.getUserId(),
                email
                );
        applicationEventPublisher.publishEvent(event);

        return orderMapper.toResponse(order);

    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Order not found with id : "+orderId));

        OrderStatus currentStatus = order.getStatus();

        if(!isTransitionValid(currentStatus,newStatus)){
            throw new BadRequestException("Order cannot transition from "+ currentStatus+" to "+newStatus);
        }

        order.setStatus(newStatus);

    }

    @Override
    public void confirmOrder(Long orderId, Long userId) {

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id : " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order cannot be confirmed in status : "+order.getStatus());
        }

        order.setStatus(OrderStatus.CONFIRMED);
    }

    @Override
    public boolean hasDeliveredProduct(Long userId, Long productId) {

        return orderItemRepository
                .existsByUserAndProductAndOrderStatus(userId,productId,OrderStatus.DELIVERED);

    }

    @Override
    public AdminOrderStatistics getOrderCount() {

        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        return new AdminOrderStatistics(
                totalOrders,
                pendingOrders,
                cancelledOrders);

    }

    private boolean isTransitionValid(OrderStatus currentStatus,OrderStatus newStatus){

        return switch(currentStatus){

            case PENDING -> newStatus==OrderStatus.CANCELLED;

            case CONFIRMED -> newStatus==OrderStatus.PROCESSING || newStatus==OrderStatus.CANCELLED;

            case PROCESSING -> newStatus==OrderStatus.SHIPPED || newStatus==OrderStatus.CANCELLED;

            case SHIPPED -> newStatus==OrderStatus.DELIVERED;

            case DELIVERED,CANCELLED -> false;

        };

    }


}
