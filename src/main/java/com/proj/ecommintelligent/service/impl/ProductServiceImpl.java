package com.proj.ecommintelligent.service.impl;

import com.proj.ecommintelligent.AI.ProductDescriptionService;
import com.proj.ecommintelligent.entities.Product;
import com.proj.ecommintelligent.repository.ProductRepository;
import com.proj.ecommintelligent.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductDescriptionService productDescriptionService;

    public ProductServiceImpl(ProductRepository productRepository, ProductDescriptionService productDescriptionService) {
        this.productRepository = productRepository;
        this.productDescriptionService = productDescriptionService;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        product.setDescription(productDescriptionService.generateDescription(
            product.getName(), 
            product.getCategory(), 
            product.getPrice(), 
            "Quantity available: " + product.getQuantity()
        ));
        return productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Optional<Product> update(Product product) {
        return Optional.of(productRepository.save(product));
    }

    @Override
    public String generateDescription(Long productId) {
        Optional<Product> productOpt = findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            String additionalAttributes = "Quantité disponible: " + product.getQuantity();
            return productDescriptionService.generateDescription(
                product.getName(), 
                product.getCategory(), 
                product.getPrice(), 
                additionalAttributes
            );
        }
        return "Produit non trouvé";
    }


}
