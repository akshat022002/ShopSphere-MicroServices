package com.akshat.order.order.controller;

import com.akshat.order.common.response.ApiResponse;
import com.akshat.order.order.dto.AdminOrderStatistics;
import com.akshat.order.order.dto.OrderResponse;
import com.akshat.order.order.dto.OrderSummaryResponse;
import com.akshat.order.order.dto.UpdateOrderStatusRequest;
import com.akshat.order.order.service.OrderService;
import com.akshat.order.security.userprovider.CurrentUserProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")

public class OrderController {

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider;

    @Value("${internal.service-secret}")
    private String serviceSecret;

    @Value("${internal.dashboard-service-secret}")
    private String dashboardServiceSecret;


    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(@RequestParam(name = "couponCode",required = false) String couponCode,
                                                                 Authentication authentication,
                                                                 @RequestHeader("Authorization") String token){


        OrderResponse response = orderService.placeOrder(
                currentUserProvider.getUserId(authentication),
                couponCode,
                token,
                currentUserProvider.getUserEmail(authentication));

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(true,
                "Order placed successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long orderId,
                                                                   Authentication authentication){


        OrderResponse response = orderService.getOrderById(currentUserProvider.getUserId(authentication), orderId);

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(true,
                "Order fetched successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getMyOrders(@RequestParam(defaultValue = "0")
                                                                               @Min(0) int page,

                                                                               @RequestParam(defaultValue = "10")
                                                                               @Min(1)@Max(20) int size,

                                                                               Authentication authentication
    ){

        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"createdAt"));

        Page<OrderSummaryResponse> response = orderService
                .getMyOrders(currentUserProvider.getUserId(authentication),pageable);

        ApiResponse<Page<OrderSummaryResponse>> apiResponse = new ApiResponse<>(true,
                "Orders fetched successfully",
                response
                ,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long orderId,
                                                                  Authentication authentication){


        OrderResponse response = orderService.cancelOrder(
                currentUserProvider.getUserId(authentication),
                orderId,
                currentUserProvider.getUserEmail(authentication));

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(true,
                "Order cancelled successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@RequestBody @Valid UpdateOrderStatusRequest request,
                                                          @PathVariable Long orderId){

        orderService.updateOrderStatus(orderId,request.status());

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Order status updated successfully",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);

    }

    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmOrder(
            @PathVariable Long orderId,
            Authentication authentication,
            @RequestHeader("X-Service-Secret") String requestedSecret) {

        if(!serviceSecret.equals(requestedSecret)){
           throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Long userId = currentUserProvider.getUserId(authentication);

        orderService.confirmOrder(orderId, userId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true,
                "Order confirmed successfully",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/verify-purchase/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> verifyPurchase(@PathVariable Long productId ,
                                                               Authentication authentication){

       boolean purchased = orderService.hasDeliveredProduct(currentUserProvider.getUserId(authentication),productId);

        ApiResponse<Boolean> apiResponse = new ApiResponse<>(
                true,
                "Purchase verified successfully",
                purchased,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);

    }

    @GetMapping("/dashboard/admin")
    public ResponseEntity<ApiResponse<AdminOrderStatistics>> getOrderCount(@RequestHeader("X-Service-Secret")
                                                                           String requestedSecret){

        if(!dashboardServiceSecret.equals(requestedSecret)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        AdminOrderStatistics response = orderService.getOrderCount();

        ApiResponse<AdminOrderStatistics> apiResponse = new ApiResponse<>(
                true,
                "Order count for admin fetched successfully",
                response,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);

    }


}
