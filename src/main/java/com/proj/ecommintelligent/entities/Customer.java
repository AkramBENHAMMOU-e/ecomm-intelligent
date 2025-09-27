package com.proj.ecommintelligent.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity @Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customer_id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String phone;
    @OneToMany(mappedBy = "customer" , cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders;
    @OneToOne(mappedBy = "customer")
    @JsonIgnore
    private Cart cart;

}
