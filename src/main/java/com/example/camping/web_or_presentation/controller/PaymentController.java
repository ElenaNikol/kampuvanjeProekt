package com.example.camping.web_or_presentation.controller;

import com.example.camping.model.Category;
import com.example.camping.model.Manufacturer;
import com.example.camping.model.Product;
import com.example.camping.model.ShoppingCart;
import com.example.camping.model.dto.ChargeRequest;
import com.example.camping.model.exception.ProductIsAlreadyInShoppingCartException;
import com.example.camping.service_or_business.AuthService;
import com.example.camping.service_or_business.CategoryService;
import com.example.camping.service_or_business.ManufacturerService;
import com.example.camping.service_or_business.ShoppingCartService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Value("${STRIPE_P_KEY}")
    private String publicKey;

    private final ShoppingCartService shoppingCartService;
    private final AuthService authService;
    private CategoryService categoryService;
    private ManufacturerService manufacturerService;

    public PaymentController(ShoppingCartService shoppingCartService,
                             AuthService authService,CategoryService categoryService,
                             ManufacturerService manufacturerService) {
        this.shoppingCartService = shoppingCartService;
        this.authService = authService;
        this.categoryService = categoryService;
        this.manufacturerService = manufacturerService;
    }


    @GetMapping("/charge")
    public String getCheckoutPage(Model model) {
        try {
            ShoppingCart shoppingCart = this.shoppingCartService.findActiveShoppingCartByUsername(this.authService.getCurrentUserId());
            model.addAttribute("shoppingCart", shoppingCart);
            model.addAttribute("currency", "eur");
            model.addAttribute("amount", (int) (shoppingCart.getProducts().stream().mapToDouble(Product::getPrice).sum() * 100));
            model.addAttribute("stripePublicKey", this.publicKey);
            List<Category> categories = this.categoryService.findAll();
            model.addAttribute("categories", categories);
            List<Manufacturer> manufacturers = this.manufacturerService.findAll();
            model.addAttribute("manufacturers", manufacturers);
            return "checkout";
        } catch (RuntimeException ex) {
            return "redirect:/products?error=" + ex.getLocalizedMessage();
        }
    }

    @PostMapping("/charge")
    public String checkout(ChargeRequest chargeRequest, Model model) {
        try {
            ShoppingCart shoppingCart = this.shoppingCartService.checkoutShoppingCart(this.authService.getCurrentUserId(), chargeRequest);
            return "redirect:/products?message=Successful Payment";
        } catch (RuntimeException ex) {
            return "redirect:/payments/charge?error=" + ex.getLocalizedMessage();
        }
    }
}
