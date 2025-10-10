package com.proj.ecommintelligent.service;

import com.proj.ecommintelligent.entities.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

@Service
public class WebhookService {

    // 1. Spring injecte l'URL depuis le fichier .properties
    @Value("${n8n.webhook.order-confirmation}")
    private String webhookUrl;

    // 2. L'outil pour envoyer des requêtes HTTP
    private final RestTemplate restTemplate = new RestTemplate();

    @Async // 3. Se lance en arrière-plan pour ne pas ralentir l'utilisateur
    public void triggerOrderConfirmation(Order order) {

        // Prépare l'en-tête pour dire qu'on envoie du JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 4. Met la commande (l'objet Order) dans le "carton" de la requête
        HttpEntity<Order> request = new HttpEntity<>(order, headers);

        try {
            // 5. Envoie la requête POST à l'URL de n8n
            restTemplate.postForObject(webhookUrl, request, String.class);
        } catch (Exception e) {
            // Gère les erreurs si n8n est inaccessible
        }
    }
}