package com.akshat.payment.payment.repository;


import com.akshat.payment.payment.entity.Payment;
import com.akshat.payment.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

        Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

        boolean existsByOrderId(Long orderId);

        @Query("""
                SELECT COALESCE(SUM(p.amount),0)
                FROM Payment p
                WHERE p.status = :status
               """)
        BigDecimal getTotalRevenueByStatus(PaymentStatus status);

}
