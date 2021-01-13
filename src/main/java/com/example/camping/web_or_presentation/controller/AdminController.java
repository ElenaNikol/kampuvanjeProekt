package com.example.camping.web_or_presentation.controller;

import com.example.camping.model.Category;
import com.example.camping.model.Manufacturer;
import com.example.camping.service_or_business.CategoryService;
import com.example.camping.service_or_business.ManufacturerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private CategoryService categoryService;
    private ManufacturerService manufacturerService;

    public AdminController(CategoryService categoryService, ManufacturerService manufacturerService) {
        this.categoryService = categoryService;
        this.manufacturerService = manufacturerService;
    }

    @GetMapping
    public String getAdminPage(Model model) {
        List<Category> categories = this.categoryService.findAll();
        model.addAttribute("categories", categories);
        List<Manufacturer> manufacturers = this.manufacturerService.findAll();
        model.addAttribute("manufacturers", manufacturers);
        return "home";
    }
}
