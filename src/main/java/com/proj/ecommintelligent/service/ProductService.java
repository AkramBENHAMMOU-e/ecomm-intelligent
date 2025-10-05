package com.proj.ecommintelligent.service;

import com.proj.ecommintelligent.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
    Optional<Product> update(Product product);
    String generateDescription(Long productId);
}
