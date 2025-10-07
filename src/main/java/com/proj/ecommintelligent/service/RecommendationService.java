package com.proj.ecommintelligent.service;

import com.proj.ecommintelligent.entities.Order;
import com.proj.ecommintelligent.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;

@Service
public class RecommendationService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Long> getRecommendations(Long productId, int limit) {

        List<Order> ordersWithProduct = orderRepository.findAllByItemsProductId(productId);
        List<Long> coPurchasedProductIds = ordersWithProduct.stream()
                //Pour chaque commande, on prend la liste de ses items
                .flatMap(order -> order.getItems().stream())
                //On prend l'ID du produit de chaque item
                .map(item -> item.getProduct().getId())
                //On s'assure de ne pas inclure le produit de base lui-même dans ses propres recommandations
                .filter(id -> !id.equals(productId))
                .collect(Collectors.toList());

        //Compter la fréquence de chaque produit co-acheté.
        Map<Long, Long> frequencyMap = coPurchasedProductIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return frequencyMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}