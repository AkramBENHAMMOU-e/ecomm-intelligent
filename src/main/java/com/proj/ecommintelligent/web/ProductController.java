package com.proj.ecommintelligent.web;

import com.proj.ecommintelligent.entities.Product;
import com.proj.ecommintelligent.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id){
        return productRepository.findById(id).orElse(null);
    }

     @PostMapping
    public void addProduct(@RequestBody Product product){
        productRepository.save(product);
    }

}
