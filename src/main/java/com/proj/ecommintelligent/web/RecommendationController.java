package com.proj.ecommintelligent.web;

import com.proj.ecommintelligent.entities.Product;
import com.proj.ecommintelligent.repository.ProductRepository;
import com.proj.ecommintelligent.service.RecommendationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/product/{productId}")
    public List<Product> getProductRecommendations(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Long> recommendedIds = recommendationService.getRecommendations(productId, limit);
        
        // findAllById ne préserve pas l'ordre, donc on doit le réorganiser
        List<Product> products = productRepository.findAllById(recommendedIds);
        
        // Créer une Map pour accès rapide par ID
        java.util.Map<Long, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, p -> p));
        
        // Retourner les produits dans l'ordre des IDs recommandés
        return recommendedIds.stream()
            .map(productMap::get)
            .filter(p -> p != null)
            .collect(Collectors.toList());
    }

    @GetMapping("/popular")
    public List<Product> getPopularProducts(
        @RequestParam(defaultValue = "5") int limit) {
        List<Long> popularIds = recommendationService.getPopularProducts(limit);
        
        // findAllById ne préserve pas l'ordre, donc on doit le réorganiser
        List<Product> products = productRepository.findAllById(popularIds);
        
        // Créer une Map pour accès rapide par ID
        java.util.Map<Long, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, p -> p));
        
        // Retourner les produits dans l'ordre des IDs populaires
        return popularIds.stream()
            .map(productMap::get)
            .filter(p -> p != null)
            .collect(Collectors.toList());
    }
}