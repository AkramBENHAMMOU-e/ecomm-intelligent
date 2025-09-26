package com.proj.ecommintelligent;

import com.proj.ecommintelligent.entities.Product;
import com.proj.ecommintelligent.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class EcommIntelligentApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommIntelligentApplication.class, args);
    }
    @Bean
    CommandLineRunner commandeLineRunner(ProductRepository productRepository){
        return args->{
            List<Product> products = List.of(
                    Product.builder()
                            .name("Laptop")
                            .description("core i5 hp")
                            .image("okk.jpg")
                            .price(122)
                            .quantity(10)
                            .category("Pc")
                            .build(),
                    Product.builder()
                            .name("Phone")
                            .description("samsung i44")
                            .image("ok1.jpg")
                            .price(133)
                            .quantity(19)
                            .category("Phones")
                            .build()



            );
            productRepository.saveAll(products);
        };
    }


}
