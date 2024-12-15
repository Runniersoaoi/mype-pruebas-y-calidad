package com.example.demo;

import com.example.demo.service.InventoryService;
import com.example.demo.service.SalesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class NonFunctionalTests {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private SalesService salesService;

    @Test
    void testSaleResponseTime() {
        inventoryService.addProduct("FastProduct", 100);
        Instant start = Instant.now();
        salesService.registerSale("FastProduct", 1);
        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();
        assertTrue(timeElapsed < 1000, "Sale processing took longer than 1 second");
    }

    @Test
    void testReportGenerationResourceUsage() {
        // Add 1000 products
        for (int i = 0; i < 1000; i++) {
            inventoryService.addProduct("Product" + i, i);
        }

        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.nanoTime();

        var products = inventoryService.getAllProducts();

        long endTime = System.nanoTime();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();

        long memoryUsed = usedMemoryAfter - usedMemoryBefore;
        long timeTaken = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println("Memory used: " + memoryUsed + " bytes");
        System.out.println("Time taken: " + timeTaken + " ms");

        assertTrue(memoryUsed < 10_000_000, "Memory usage exceeds 10MB");
        assertTrue(timeTaken < 2000, "Report generation took longer than 2 seconds");
    }

    @Test
    void testLargeInventoryPerformance() {
        // Add 10,000 products
        for (int i = 0; i < 10000; i++) {
            inventoryService.addProduct("LargeProduct" + i, i);
        }

        Instant start = Instant.now();
        var product = inventoryService.getProductByName("LargeProduct5000");
        inventoryService.updateStock("LargeProduct5000", product.getQuantity() + 1);
        Instant end = Instant.now();

        long timeElapsed = Duration.between(start, end).toMillis();
        assertTrue(timeElapsed < 2000, "Large inventory operation took longer than 2 seconds");
    }

    @Test
    void testInvalidInputHandling() {
        inventoryService.addProduct("TestProduct", 10);
        assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.updateStock("TestProduct", -5);
        });
        var product = inventoryService.getProductByName("TestProduct");
        assertEquals(10, product.getQuantity(), "Stock should not have changed");
    }
}

