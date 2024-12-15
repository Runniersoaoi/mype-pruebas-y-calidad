package com.example.demo.service;

import com.example.demo.Product;
import com.example.demo.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final ProductRepository productRepository;

    @Transactional
    public void addProduct(String name, int quantity) {
        Product product = productRepository.findByName(name);
        if (product == null) {
            product = new Product();
            product.setName(name);
            product.setQuantity(quantity);
        } else {
            product.setQuantity(product.getQuantity() + quantity);
        }
        productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Transactional
    public void updateStock(String name, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        Product product = productRepository.findByName(name);
        if (product != null) {
            product.setQuantity(newQuantity);
            productRepository.save(product);
        } else {
            throw new IllegalArgumentException("Product not found: " + name);
        }
    }
}