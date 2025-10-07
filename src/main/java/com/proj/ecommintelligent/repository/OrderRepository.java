package com.proj.ecommintelligent.repository;

import com.proj.ecommintelligent.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN o.items i WHERE i.product.id = :productId")
    List<Order> findAllByItemsProductId(@Param("productId") Long productId);
}