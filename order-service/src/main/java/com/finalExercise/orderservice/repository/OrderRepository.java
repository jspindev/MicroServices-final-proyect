package com.finalExercise.orderservice.repository;

import com.finalExercise.orderservice.model.Order;
import com.finalExercise.orderservice.model.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    void deleteByOrderNumber(String orderNumber);
    List<Order> findByOrderLineItemsList_SkuCode(String skuCode);

    @Query("SELECT oli FROM Order o JOIN o.orderLineItemsList oli WHERE o.orderNumber = :orderNumber AND oli.skuCode = :skuCode")
    Optional<OrderLineItem> findOrderLineItemByOrderNumberAndSkuCode(@Param("orderNumber") String orderNumber, @Param("skuCode") String skuCode);


}
