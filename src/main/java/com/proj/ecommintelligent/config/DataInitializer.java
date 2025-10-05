package com.proj.ecommintelligent.config;

import com.proj.ecommintelligent.entities.Cart;
import com.proj.ecommintelligent.entities.Product;
import com.proj.ecommintelligent.repository.CartRepository;
import com.proj.ecommintelligent.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public DataInitializer(ProductRepository productRepository, CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create a default cart with ID 1 if none exists
        if (cartRepository.count() == 0) {
            Cart defaultCart = Cart.builder().items(new java.util.ArrayList<>()).build();
            Cart savedCart = cartRepository.save(defaultCart);
            System.out.println("Default cart created with ID: " + savedCart.getId());
        }

        // Create sample products for testing
        if (productRepository.count() == 0) {
            Product laptop = Product.builder()
                    .name("MacBook Pro M3")
                    .category("Informatique")
                    .price(2499.99)
                    .quantity(10)
                    .image("macbook-pro.jpg")
                    .description("")  // Empty description for AI generation
                    .build();

            Product smartphone = Product.builder()
                    .name("iPhone 15 Pro")
                    .category("Téléphonie")
                    .price(1199.99)
                    .quantity(25)
                    .image("iphone-15-pro.jpg")
                    .description("")  // Empty description for AI generation
                    .build();

            Product headphones = Product.builder()
                    .name("AirPods Pro 2")
                    .category("Audio")
                    .price(279.99)
                    .quantity(50)
                    .image("airpods-pro.jpg")
                    .description("")  // Empty description for AI generation
                    .build();

            productRepository.save(laptop);
            productRepository.save(smartphone);
            productRepository.save(headphones);

            System.out.println("Sample products created!");
        }
    }
}