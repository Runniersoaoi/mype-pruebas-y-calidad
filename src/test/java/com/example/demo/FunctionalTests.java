package com.example.demo;

import com.example.demo.service.InventoryService;
import com.example.demo.service.SalesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FunctionalTests {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private SalesService salesService;

    @BeforeEach
    void setUp() {
        // Initialize some products
        inventoryService.addProduct("Mouse", 10);
        inventoryService.addProduct("Teclado", 5);
        inventoryService.addProduct("Monitor", 10);
    }

    @Test
    void testAddNewProduct() {
        inventoryService.addProduct("Laptop", 10);
        Product laptop = inventoryService.getProductByName("Laptop");
        assertNotNull(laptop);
        assertEquals(10, laptop.getQuantity());
    }

    @Test
    void testUpdateStock() {
        inventoryService.addProduct("Mouse", 5);
        Product mouse = inventoryService.getProductByName("Mouse");
        assertEquals(15, mouse.getQuantity());
    }

    @Test
    void testSuccessfulSale() {
        salesService.registerSale("Teclado", 3);
        Product teclado = inventoryService.getProductByName("Teclado");
        assertEquals(2, teclado.getQuantity());
    }

    @Test
    void testInsufficientStock() {
        assertThrows(IllegalStateException.class, () -> {
            salesService.registerSale("Monitor", 15);
        });
        Product monitor = inventoryService.getProductByName("Monitor");
        assertEquals(10, monitor.getQuantity());
    }

    @Test
    void testGenerateInventoryReport() {
        var products = inventoryService.getAllProducts();
        assertFalse(products.isEmpty());
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Mouse")));
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Teclado")));
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Monitor")));
    }

    @Test
    void testGenerateSalesReport() {
        salesService.registerSale("Mouse", 2);
        salesService.registerSale("Teclado", 1);
        var sales = salesService.getAllSales();
        assertFalse(sales.isEmpty());
        assertEquals(3, salesService.getTotalSales());
    }
}