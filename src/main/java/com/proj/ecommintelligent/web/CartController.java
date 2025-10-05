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
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
        // Try to find the cart
        Cart cart = cartService.findById(id).orElse(null);
        
        // If cart not found, create a new one
        if (cart == null) {
            cart = new Cart();
            cart.setItems(new ArrayList<>());
            cart = cartService.save(cart);
        }
        
        return cart;
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
        // Get the cart or create it if it doesn't exist
        Cart cart = cartService.findById(cartId).orElse(null);
        if (cart == null) {
            cart = new Cart();
            cart.setItems(new ArrayList<>());
            cart = cartService.save(cart);
        }
        
        // Find the product
        Product product = productService.findById(request.productId).orElse(null);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        // Check quantity
        if (request.quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");
        }

        // Make sure items list exists
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        // Check if product already exists in cart
        CartItem existingItem = null;
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                existingItem = item;
                break;
            }
        }
        
        // If product exists, increase quantity
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.quantity);
        } else {
            // If product doesn't exist, add new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.quantity);
            cart.getItems().add(newItem);
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
        // Check if cart exists
        if (cartService.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        }
        cartService.deleteById(id);
    }

    /**
     * Requête d'ajout d'item au panier.
     * Champs:
     * - productId: identifiant du produit
     * - quantity: quantité à ajouter (>0)
     */
    public static class AddItemRequest {
        public Long productId;
        public int quantity;
        
        // Default constructor
        public AddItemRequest() {}
        
        // Constructor with parameters
        public AddItemRequest(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}