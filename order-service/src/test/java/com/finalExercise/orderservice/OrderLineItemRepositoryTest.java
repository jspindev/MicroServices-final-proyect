package com.finalExercise.orderservice;


import com.finalExercise.orderservice.model.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineItemRepositoryTest extends JpaRepository<OrderLineItem,Long> {
}
