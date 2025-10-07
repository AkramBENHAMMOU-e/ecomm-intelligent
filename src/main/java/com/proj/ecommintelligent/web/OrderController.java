package com.proj.ecommintelligent.web;

import com.proj.ecommintelligent.entities.Cart;
import com.proj.ecommintelligent.entities.CartItem;
import com.proj.ecommintelligent.entities.Order;
import com.proj.ecommintelligent.entities.OrderItem;
import com.proj.ecommintelligent.enums.StatusOrder;
import com.proj.ecommintelligent.service.CartService;
import com.proj.ecommintelligent.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("api/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Order> getAllOrders(){
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Order getOrderById(@PathVariable Long id){
        return orderService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Order addOrder(@RequestBody Order order){
        return orderService.save(order);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        Order existingOrder = orderService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        
        // Update allowed fields
        if (updatedOrder.getStatus() != null) {
            existingOrder.setStatus(updatedOrder.getStatus());
        }
        
        if (updatedOrder.getDate() != null) {
            existingOrder.setDate(updatedOrder.getDate());
        }
        
        // Note: We're not updating items or customer to avoid complexity
        // In a real application, you might want more sophisticated logic here
        
        return orderService.save(existingOrder);
    }

    @PostMapping("/checkout")
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    public Order checkout(@RequestParam Long cartId) {
        Cart cart = cartService.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = Order.builder()
                .date(LocalDate.now())
                .status(StatusOrder.Processing)
                .customer(cart.getCustomer())
                .items(new ArrayList<>())
                .build();

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(ci.getProduct())
                    .quantity(ci.getQuantity())
                    .price(ci.getProduct().getPrice())
                    .build();
            order.getItems().add(oi);
        }

        Order saved = orderService.save(order);

        // Important: with orphanRemoval=true on Cart.items, do NOT replace the collection reference.
        // Clear it in place so Hibernate can track orphan deletions without throwing.
        if (cart.getItems() != null) {
            cart.getItems().clear();
        } else {
            cart.setItems(new ArrayList<>());
        }
        cartService.save(cart);

        return saved;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id){
        if (orderService.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        orderService.deleteById(id);
    }
}