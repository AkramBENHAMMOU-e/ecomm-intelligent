package com.proj.ecommintelligent;

import com.proj.ecommintelligent.entities.Cart;
import com.proj.ecommintelligent.entities.CartItem;
import com.proj.ecommintelligent.entities.Product;
import com.proj.ecommintelligent.repository.CartRepository;
import com.proj.ecommintelligent.repository.ProductRepository;
import com.proj.ecommintelligent.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import com.proj.ecommintelligent.enums.StatusOrder;
import com.proj.ecommintelligent.entities.Order;
import com.proj.ecommintelligent.entities.OrderItem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class EcommIntelligentApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommIntelligentApplication.class, args);
    }

    @Bean
    CommandLineRunner commandeLineRunner(ProductRepository productRepository, CartRepository cartRepository, OrderRepository orderRepository){
        return args->{
           if (productRepository.count() == 0) {
                List<Product> products = List.of(
                        Product.builder().name("iPhone 14").description("Smartphone Apple 6.1'' A15 Bionic").category("Phones").image("https://via.placeholder.com/200x200?text=iPhone14").price(12999).quantity(25).build(),
                        Product.builder().name("iPhone 14 Case").description("Coque silicone pour iPhone 14").category("Accessories").image("https://via.placeholder.com/200x200?text=iPhone+Case").price(199).quantity(200).build(),
                        Product.builder().name("AirPods Pro").description("Écouteurs sans fil avec ANC").category("Audio").image("https://via.placeholder.com/200x200?text=AirPods+Pro").price(2299).quantity(80).build(),
                        Product.builder().name("Galaxy S23").description("Smartphone Samsung 6.1'' Snapdragon 8 Gen 2").category("Phones").image("https://via.placeholder.com/200x200?text=Galaxy+S23").price(10999).quantity(30).build(),
                        Product.builder().name("Galaxy Buds2").description("Écouteurs true wireless").category("Audio").image("https://via.placeholder.com/200x200?text=Buds2").price(899).quantity(120).build(),
                        Product.builder().name("MacBook Air M2").description("Laptop Apple 13'' puce M2").category("Laptops").image("https://via.placeholder.com/200x200?text=MacBook+Air+M2").price(14999).quantity(15).build(),
                        Product.builder().name("Dell XPS 13").description("Ultrabook 13'' Intel i7").category("Laptops").image("https://via.placeholder.com/200x200?text=Dell+XPS+13").price(13999).quantity(12).build(),
                        Product.builder().name("Logitech MX Master 3").description("Souris sans fil haut de gamme").category("Accessories").image("https://via.placeholder.com/200x200?text=MX+Master+3").price(799).quantity(60).build(),
                        Product.builder().name("USB‑C Hub 7‑in‑1").description("Adaptateur USB‑C multi‑ports").category("Accessories").image("https://via.placeholder.com/200x200?text=USB-C+Hub").price(349).quantity(100).build(),
                        Product.builder().name("iPad 10e Gen").description("Tablette 10.9'' A14").category("Tablets").image("https://via.placeholder.com/200x200?text=iPad+10G").price(5999).quantity(20).build()
                );
                productRepository.saveAll(products);
            }

            // Build a helper map by name
            List<Product> all = productRepository.findAll();
            Map<String, Product> byName = all.stream().collect(Collectors.toMap(p -> p.getName().toLowerCase(Locale.ROOT), p -> p, (a,b)->a));

            if (orderRepository.count() == 0 && !all.isEmpty()) {
                // Create several orders with overlapping items
                List<Order> sampleOrders = new ArrayList<>();

                // Order 1: iPhone 14 + Case + AirPods Pro
                sampleOrders.add(createOrder(LocalDate.now().minusDays(10), StatusOrder.Completed,
                        item(of(byName, "iphone 14"), 1),
                        item(of(byName, "iphone 14 case"), 1),
                        item(of(byName, "airpods pro"), 1)
                ));

                // Order 2: Galaxy S23 + Buds2 + Case (case from accessories)
                sampleOrders.add(createOrder(LocalDate.now().minusDays(8), StatusOrder.Shipped,
                        item(of(byName, "galaxy s23"), 1),
                        item(of(byName, "galaxy buds2"), 1),
                        item(of(byName, "iphone 14 case"), 1) // overlap on accessories category
                ));

                // Order 3: MacBook Air M2 + USB-C Hub + MX Master 3
                sampleOrders.add(createOrder(LocalDate.now().minusDays(6), StatusOrder.Processing,
                        item(of(byName, "macbook air m2"), 1),
                        item(of(byName, "usb‑c hub 7‑in‑1"), 1),
                        item(of(byName, "logitech mx master 3"), 1)
                ));

                // Order 4: Dell XPS 13 + MX Master 3 (overlap with Order 3)
                sampleOrders.add(createOrder(LocalDate.now().minusDays(4), StatusOrder.Processing,
                        item(of(byName, "dell xps 13"), 1),
                        item(of(byName, "logitech mx master 3"), 1)
                ));

                // Order 5: iPhone 14 + AirPods Pro (overlap with Order 1)
                sampleOrders.add(createOrder(LocalDate.now().minusDays(2), StatusOrder.Processing,
                        item(of(byName, "iphone 14"), 1),
                        item(of(byName, "airpods pro"), 1)
                ));

                // Order 6: iPad + USB‑C Hub (overlap on hub with Order 3)
                sampleOrders.add(createOrder(LocalDate.now().minusDays(1), StatusOrder.Processing,
                        item(of(byName, "ipad 10e gen"), 1),
                        item(of(byName, "usb‑c hub 7‑in‑1"), 1)
                ));

                orderRepository.saveAll(sampleOrders);
            }
        };
    }

    private static Product of(Map<String, Product> byName, String key) {
        return byName.getOrDefault(key.toLowerCase(Locale.ROOT), null);
    }

    private static Order createOrder(LocalDate date, StatusOrder status, OrderItem... items) {
        Order order = Order.builder()
                .date(date)
                .status(status)
                .items(new ArrayList<>())
                .build();
        for (OrderItem it : items) {
            if (it == null || it.getProduct() == null) continue;
            it.setOrder(order);
            // Ensure price snapshot is set
            if (it.getPrice() <= 0 && it.getProduct() != null) {
                it.setPrice(it.getProduct().getPrice());
            }
            order.getItems().add(it);
        }
        return order;
    }

    private static OrderItem item(Product p, int qty) {
        if (p == null) return null;
        return OrderItem.builder()
                .product(p)
                .quantity(qty)
                .price(p.getPrice())
                .build();
    }
}
