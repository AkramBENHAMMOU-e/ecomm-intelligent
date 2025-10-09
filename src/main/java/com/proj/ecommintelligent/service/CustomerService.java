package com.proj.ecommintelligent.service;

import com.proj.ecommintelligent.entities.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAll();
    Optional<Customer> findById(Long id);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhone(String phone);
    Customer save(Customer customer);
    Customer createOrFindCustomer(String firstName, String lastName, String email, String phone, String address);
    void deleteById(Long id);
}
