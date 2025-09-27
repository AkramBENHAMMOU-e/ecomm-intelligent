package com.proj.ecommintelligent.web;

import com.proj.ecommintelligent.entities.Cart;
import com.proj.ecommintelligent.entities.CartItem;
import com.proj.ecommintelligent.entities.Product;
import com.proj.ecommintelligent.service.CartService;
import com.proj.ecommintelligent.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur Paniers
 * Endpoints pour créer, consulter et modifier les paniers.
 * Base path: /api/carts
 */
@RestController
@RequestMapping("api/carts")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    /**
     * GET /api/carts
     * Liste tous les paniers.
     * Réponse: 200 OK + JSON array de Cart
     */
    @GetMapping
    public List<Cart> getAllCarts(){
        return cartService.findAll();
    }

    /**
     * GET /api/carts/{id}
     * Récupère un panier par identifiant.
     * Paramètres: path id (Long)
     * Réponses: 200 OK + Cart, 404 si non trouvé
     */
    @GetMapping("/{id}")
    public Cart getCartById(@PathVariable Long id){
        return cartService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    /**
     * POST /api/carts
     * Crée un nouveau panier (optionnellement avec customer et items).
     * Corps: JSON Cart
     * Réponse: 201 Created + Cart créé
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cart addCart(@RequestBody Cart cart){
        return cartService.save(cart);
    }

    /**
     * POST /api/carts/{cartId}/items
     * Ajoute un produit au panier (ou augmente la quantité si déjà présent).
     * Paramètres: path cartId (Long)
     * Corps: { "productId": Long, "quantity": int (>0) }
     * Réponses: 201 Created + Cart mis à jour, 404 si cart/product introuvable, 400 si quantité invalide
     */
    @PostMapping("/{cartId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Cart addItemToCart(@PathVariable Long cartId, @RequestBody AddItemRequest request) {
        Cart cart = cartService.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        Product product = productService.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");
        }

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        // If the product already exists in cart, increase quantity
        CartItem existing = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.quantity());
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .build();
            cart.getItems().add(item);
        }

        return cartService.save(cart);
    }

    /**
     * DELETE /api/carts/{id}
     * Supprime un panier par son identifiant.
     * Paramètres: path id (Long)
     * Réponses: 204 No Content si supprimé, 404 si non trouvé
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCart(@PathVariable Long id){
        if (cartService.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        }
        cartService.deleteById(id);
    }

    /**
         * Requête d’ajout d’item au panier.
         * Champs:
         * - productId: identifiant du produit
         * - quantity: quantité à ajouter (>0)
         */
        public record AddItemRequest(Long productId, int quantity) {}
}
