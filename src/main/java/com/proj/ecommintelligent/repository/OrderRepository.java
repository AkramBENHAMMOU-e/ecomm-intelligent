package com.proj.ecommintelligent.repository;

import com.proj.ecommintelligent.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}