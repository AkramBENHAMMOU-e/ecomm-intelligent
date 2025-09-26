package com.proj.ecommintelligent.repository;

import com.proj.ecommintelligent.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product , Long> {
}
