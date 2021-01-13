package com.example.camping.web_or_presentation.controller;

import com.example.camping.model.Category;
import com.example.camping.model.Manufacturer;
import com.example.camping.model.Product;
import com.example.camping.model.exception.ProductIsAlreadyInShoppingCartException;
import com.example.camping.persistence_or_repository.CategoryRepository;
import com.example.camping.service_or_business.CategoryService;
import com.example.camping.service_or_business.ManufacturerService;
import com.example.camping.service_or_business.ProductService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {


    private ProductService productService;
    private ManufacturerService manufacturerService;
    private CategoryRepository categoryRepository;
    private CategoryService categoryService;

    public ProductController(ProductService productService,
                             ManufacturerService manufacturerService,
                             CategoryRepository categoryRepository,
                             CategoryService categoryService) {
        this.productService = productService;
        this.manufacturerService = manufacturerService;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }


    @GetMapping
    public String getProductPage(Model model) {
        List<Category> categories = this.categoryService.findAll();
        List<Product> products = this.productService.findAll();
        List<Manufacturer> manufacturers = this.manufacturerService.findAll();
        model.addAttribute("categories",categories);
        model.addAttribute("products", products);
        model.addAttribute("manufacturers",manufacturers);
        return "products";
    }

    @GetMapping("/category/{categoryId}")
    public String getProductPageByCategory(Model model, @PathVariable Long categoryId) {
        List<Product> products = this.productService.findAllByCategoryId(categoryId);
        List<Category> categories = this.categoryService.findAll();
        List<Manufacturer> manufacturers = this.manufacturerService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("categories",categories);
        model.addAttribute("manufacturers",manufacturers);
        return "products";
    }

    @GetMapping("/manufacturer/{manufacturerId}")
    public String getProductPageByManufacturer(Model model, @PathVariable Long manufacturerId) {
        List<Product> products = this.productService.findAllByManufacturerId(manufacturerId);
        List<Manufacturer> manufacturers = this.manufacturerService.findAll();
        List<Category> categories = this.categoryService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("manufacturers",manufacturers);
        model.addAttribute("categories",categories);
        return "products";
    }



    @GetMapping("/add-new")
    public String addNewProductPage(Model model) {
        List<Manufacturer> manufacturers = this.manufacturerService.findAll();
        model.addAttribute("manufacturers", manufacturers);
        model.addAttribute("categories", this.categoryRepository.findAll());
        model.addAttribute("product", new Product());

        return "add-product";
    }

    @GetMapping("/{id}/edit")
    public String editProductPage(Model model, @PathVariable Long id) {
        try {
            Product product = this.productService.findById(id);
            List<Manufacturer> manufacturers = this.manufacturerService.findAll();
            model.addAttribute("product", product);
            model.addAttribute("manufacturers", manufacturers);
            model.addAttribute("categories", this.categoryRepository.findAll());
            return "add-product";
        } catch (RuntimeException ex) {
            return "redirect:/products?error=" + ex.getMessage();
        }
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public String saveProduct(
            @Valid Product product,
            BindingResult bindingResult,
            @RequestParam MultipartFile image,
            Model model) {

        if (bindingResult.hasErrors()) {
            List<Manufacturer> manufacturers = this.manufacturerService.findAll();
            model.addAttribute("manufacturers", manufacturers);
            model.addAttribute("categories", this.categoryRepository.findAll());
            return "add-product";
        }
        try {
            this.productService.saveProduct(product, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProductWithPost(@PathVariable Long id) {
        try {
            this.productService.deleteById(id);
        } catch (ProductIsAlreadyInShoppingCartException ex) {
            return String.format("redirect:/products?error=%s", ex.getMessage());
        }
        return "redirect:/products";
    }

    @DeleteMapping("/{id}/delete")
    public String deleteProductWithDelete(@PathVariable Long id) {
        this.productService.deleteById(id);
        return "redirect:/products";
    }
}
