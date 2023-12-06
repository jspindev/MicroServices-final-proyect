package com.finalExercise.orderservice.repository;

import com.finalExercise.orderservice.model.Order;
import com.finalExercise.orderservice.model.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineItemRepository extends JpaRepository<OrderLineItem,Long> {
}
