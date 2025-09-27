package com.proj.ecommintelligent;

import com.proj.ecommintelligent.entities.Cart;
import com.proj.ecommintelligent.entities.CartItem;
import com.proj.ecommintelligent.entities.Product;
import com.proj.ecommintelligent.repository.CartRepository;
import com.proj.ecommintelligent.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class EcommIntelligentApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommIntelligentApplication.class, args);
    }

    @Bean
    CommandLineRunner commandeLineRunner(ProductRepository productRepository, CartRepository cartRepository){
        return args->{
            // Seed sample products
            List<Product> products = List.of(
                    Product.builder()
                            .name("Laptop")
                            .description("HP Core i5")
                            .image("laptop.jpg")
                            .price(899.99)
                            .quantity(10)
                            .category("PC")
                            .build(),
                    Product.builder()
                            .name("Phone")
                            .description("Samsung S series")
                            .image("phone.jpg")
                            .price(499.00)
                            .quantity(25)
                            .category("Phones")
                            .build(),
                    Product.builder()
                            .name("Headphones")
                            .description("Wireless over-ear")
                            .image("headphones.jpg")
                            .price(149.50)
                            .quantity(40)
                            .category("Audio")
                            .build()
            );
            List<Product> savedProducts = productRepository.saveAll(products);

            // Create a demo cart with a couple of items (no customer for simplicity)
            Cart demoCart = Cart.builder()
                    .items(new ArrayList<>())
                    .build();

            if (!savedProducts.isEmpty()) {
                Product p1 = savedProducts.get(0);
                Product p2 = savedProducts.size() > 1 ? savedProducts.get(1) : savedProducts.get(0);

                CartItem item1 = CartItem.builder()
                        .cart(demoCart)
                        .product(p1)
                        .quantity(1)
                        .build();
                CartItem item2 = CartItem.builder()
                        .cart(demoCart)
                        .product(p2)
                        .quantity(2)
                        .build();

                demoCart.getItems().add(item1);
                demoCart.getItems().add(item2);
            }

            cartRepository.save(demoCart);
        }; 
    }
}
