package com.proj.ecommintelligent.AI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.List;
import java.time.Duration;

@Service
public class ProductDescriptionService {

    private final WebClient webClient;
    private final String apiKey;

    public ProductDescriptionService(WebClient.Builder webClientBuilder, 
                                   @Value("${gemini.api.key}") String apiKey) {
        this.webClient = webClientBuilder.build();
        this.apiKey = apiKey;
    }

    public String generateDescription(String productName, String productCategory, double productPrice, String additionalAttributes) {
        String prompt = String.format("""
                Génère une description de produit en UNE SEULE PHRASE de 15 à 25 mots maximum pour un site e-commerce.
                Produit : %s (%s) - %.2f€
                Détails : %s

                Met en avant les avantages principaux. Écris en français. 
                Texte brut sans formatage.
                """, productName, productCategory, productPrice, additionalAttributes);

        System.out.println("Génération de description pour: " + productName);
        
        try {
            // Préparer la requête pour l'API Gemini (format simplifié)
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of(
                        "parts", List.of(
                            Map.of("text", prompt)
                        )
                    )
                )
            );

            // Appel à l'API Gemini avec timeout - using the updated model
            Mono<Map> response = webClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(30));

            Map<String, Object> result = response.block();
            
            if (result != null && result.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) result.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        String generatedDescription = (String) parts.get(0).get("text");
                        System.out.println("Description générée avec succès pour: " + productName);
                        // Clean the description to ensure it's plain text and a single sentence
                        return ensureSingleSentence(cleanDescription(generatedDescription));
                    }
                }
            }
            
            System.out.println("Réponse API vide, affichage de l'erreur");
            return "Erreur : Impossible de générer la description du produit. Réponse API vide ou invalide.";
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à l'API Gemini pour " + productName + ": " + e.getMessage());
            e.printStackTrace();
            return "Erreur lors de la génération de la description : " + e.getMessage();
        }
    }
    
    /**
     * Clean the AI-generated description to ensure it's plain text
     */
    private String cleanDescription(String description) {
        // Remove HTML tags if any
        String cleaned = description.replaceAll("<[^>]*>", "");
        
        // Remove markdown formatting
        cleaned = cleaned.replaceAll("\\*\\*", "")
                        .replaceAll("__", "")
                        .replaceAll("##", "")
                        .replaceAll("#", "");
        
        // Remove extra whitespace and normalize line breaks
        cleaned = cleaned.replaceAll("\n{3,}", "\n\n")
                        .replaceAll("^\\s+|\\s+$", "")
                        .trim();
        
        return cleaned;
    }
    
    /**
     * Ensure the description is a single sentence
     */
    private String ensureSingleSentence(String description) {
        // Split by sentence-ending punctuation and take only the first sentence
        String[] sentences = description.split("[.!?]+");
        if (sentences.length > 0) {
            // Ensure the sentence ends with a period
            String sentence = sentences[0].trim();
            if (!sentence.endsWith(".")) {
                sentence += ".";
            }
            return sentence;
        }
        return description.trim();
    }
    
    /**
     * Method to list available models - for debugging purposes
     */
    public String listAvailableModels() {
        try {
            Mono<Map> response = webClient.get()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(30));
                    
            Map<String, Object> result = response.block();
            return result.toString();
        } catch (Exception e) {
            return "Erreur lors de la récupération des modèles disponibles : " + e.getMessage();
        }
    }
}