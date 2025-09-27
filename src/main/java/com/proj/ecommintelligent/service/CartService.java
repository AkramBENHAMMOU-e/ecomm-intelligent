package com.proj.ecommintelligent.service;

import com.proj.ecommintelligent.entities.Cart;

import java.util.List;
import java.util.Optional;

public interface CartService {
    List<Cart> findAll();
    Optional<Cart> findById(Long id);
    Cart save(Cart cart);
    void deleteById(Long id);
}
