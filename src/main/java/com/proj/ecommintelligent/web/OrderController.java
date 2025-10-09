package com.proj.ecommintelligent.web;

import com.proj.ecommintelligent.entities.Cart;
import com.proj.ecommintelligent.entities.CartItem;
import com.proj.ecommintelligent.entities.Customer;
import com.proj.ecommintelligent.entities.Order;
import com.proj.ecommintelligent.entities.OrderItem;
import com.proj.ecommintelligent.enums.StatusOrder;
import com.proj.ecommintelligent.service.CartService;
import com.proj.ecommintelligent.service.CustomerService;
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
    private final CustomerService customerService;

    public OrderController(OrderService orderService, CartService cartService, CustomerService customerService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.customerService = customerService;
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
        
        if (updatedOrder.getStatus() != null) {
            existingOrder.setStatus(updatedOrder.getStatus());
        }
        
        if (updatedOrder.getDate() != null) {
            existingOrder.setDate(updatedOrder.getDate());
        }
               
        return orderService.save(existingOrder);
    }

    @PostMapping("/checkout")
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    public Order checkout(@RequestBody CheckoutRequest checkoutRequest) {
        Cart cart = cartService.findById(checkoutRequest.getCartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Customer customer = customerService.createOrFindCustomer(
                checkoutRequest.getFirstName(),
                checkoutRequest.getLastName(),
                checkoutRequest.getEmail(),
                checkoutRequest.getPhone(),
                checkoutRequest.getAddress()
        );

        Order order = Order.builder()
                .date(LocalDate.now())
                .status(StatusOrder.Processing)
                .customer(customer)
                .items(new ArrayList<>())
                .phoneNumber(checkoutRequest.getPhone())
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
        if (cart.getItems() != null) {
            cart.getItems().clear();
        } else {
            cart.setItems(new ArrayList<>());
        }
        cartService.save(cart);

        return saved;
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
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