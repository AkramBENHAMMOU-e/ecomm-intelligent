package com.proj.ecommintelligent.web;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long cartId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
}
