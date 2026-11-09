package com.akshat.order.order.repository;

import com.akshat.order.order.entity.StockRestorationTask;
import com.akshat.order.order.enums.RestorationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRestorationTaskRepository extends JpaRepository<StockRestorationTask,Long> {

    List<StockRestorationTask> findByStatus(RestorationStatus status);

    List<StockRestorationTask> findByOrderIdAndStatus(Long orderId, RestorationStatus status);

}
