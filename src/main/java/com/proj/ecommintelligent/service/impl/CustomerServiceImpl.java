package com.proj.ecommintelligent.service.impl;

import com.proj.ecommintelligent.entities.Customer;
import com.proj.ecommintelligent.repository.CustomerRepository;
import com.proj.ecommintelligent.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Optional<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer createOrFindCustomer(String firstName, String lastName, String email, String phone, String address) {
        if (email != null && !email.trim().isEmpty()) {
            Optional<Customer> existingCustomer = findByEmail(email);
            if (existingCustomer.isPresent()) {
                Customer customer = existingCustomer.get();
                if (firstName != null && !firstName.trim().isEmpty()) {
                    customer.setFirstName(firstName);
                }
                if (lastName != null && !lastName.trim().isEmpty()) {
                    customer.setLastName(lastName);
                }
                if (phone != null && !phone.trim().isEmpty()) {
                    customer.setPhone(phone);
                }
                if (address != null && !address.trim().isEmpty()) {
                    customer.setAddress(address);
                }
                return save(customer);
            }
        }

        if (phone != null && !phone.trim().isEmpty()) {
            Optional<Customer> existingCustomer = findByPhone(phone);
            if (existingCustomer.isPresent()) {
                Customer customer = existingCustomer.get();
                if (firstName != null && !firstName.trim().isEmpty()) {
                    customer.setFirstName(firstName);
                }
                if (lastName != null && !lastName.trim().isEmpty()) {
                    customer.setLastName(lastName);
                }
                if (email != null && !email.trim().isEmpty()) {
                    customer.setEmail(email);
                }
                if (address != null && !address.trim().isEmpty()) {
                    customer.setAddress(address);
                }
                return save(customer);
            }
        }

        Customer newCustomer = new Customer();
        newCustomer.setFirstName(firstName != null ? firstName.trim() : "Client");
        newCustomer.setLastName(lastName != null ? lastName.trim() : "Anonyme");
        newCustomer.setEmail(email != null ? email.trim() : null);
        newCustomer.setPhone(phone != null ? phone.trim() : null);
        newCustomer.setAddress(address != null ? address.trim() : null);
        
        return save(newCustomer);
    }

    @Override
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }
}
