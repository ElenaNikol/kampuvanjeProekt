package com.example.camping.web_or_presentation.controller;

import com.example.camping.model.Category;
import com.example.camping.model.Manufacturer;
import com.example.camping.model.Product;
import com.example.camping.service_or_business.CategoryService;
import com.example.camping.service_or_business.ManufacturerService;
import com.example.camping.service_or_business.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/contact")
public class ContactController {

    private ProductService productService;
    private CategoryService categoryService;
    private ManufacturerService manufacturerService;

    public ContactController(ProductService productService,CategoryService categoryService,ManufacturerService manufacturerService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.manufacturerService = manufacturerService;
    }


    @GetMapping
    public String getContactPage(HttpServletResponse res, HttpServletRequest req,Model model) {
        List<Product> products = this.productService.findAll();
        model.addAttribute("products", products);
        List<Category> categories = this.categoryService.findAll();
        model.addAttribute("categories", categories);
        List<Manufacturer> manufacturers = this.manufacturerService.findAll();
        model.addAttribute("manufacturers", manufacturers);
        return "contact";
    }

}
