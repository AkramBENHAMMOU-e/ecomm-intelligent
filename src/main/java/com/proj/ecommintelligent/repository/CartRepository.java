package com.proj.ecommintelligent.repository;

import com.proj.ecommintelligent.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
