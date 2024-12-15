package com.example.demo.controller;

import com.example.demo.Product;
import com.example.demo.Sale;
import com.example.demo.service.InventoryService;
import com.example.demo.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MypeController {
    private final InventoryService inventoryService;
    private final SalesService salesService;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = inventoryService.getAllProducts();
        List<Sale> sales = salesService.getAllSales();
        model.addAttribute("products", products);
        model.addAttribute("sales", sales);
        model.addAttribute("totalSales", salesService.getTotalSales());
        return "home";
    }

    @PostMapping("/addProduct")
    public String addProduct(@RequestParam String name, @RequestParam int quantity) {
        inventoryService.addProduct(name, quantity);
        return "redirect:/";
    }

    @PostMapping("/registerSale")
    public String registerSale(@RequestParam String productName, @RequestParam int quantity) {
        salesService.registerSale(productName, quantity);
        return "redirect:/";
    }
}