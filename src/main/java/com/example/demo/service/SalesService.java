package com.example.demo.service;

import com.example.demo.Product;
import com.example.demo.Sale;
import com.example.demo.repository.SaleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final SaleRepository saleRepository;
    private final InventoryService inventoryService;

    @Transactional
    public void registerSale(String productName, int quantity) {
        Product product = inventoryService.getProductByName(productName);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productName);
        }
        if (product.getQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + productName);
        }

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setQuantity(quantity);
        sale.setSaleDate(LocalDateTime.now());
        saleRepository.save(sale);

        inventoryService.updateStock(productName, product.getQuantity() - quantity);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public int getTotalSales() {
        return getAllSales().stream().mapToInt(Sale::getQuantity).sum();
    }
}


