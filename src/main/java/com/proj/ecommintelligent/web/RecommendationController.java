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
        return productRepository.findAllById(recommendedIds);
    }
}