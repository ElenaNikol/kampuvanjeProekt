package com.example.camping.service_or_business.impl;

import com.example.camping.model.Product;
import com.example.camping.model.ShoppingCart;
import com.example.camping.model.User;
import com.example.camping.model.dto.ChargeRequest;
import com.example.camping.model.enumerations.CartStatus;
import com.example.camping.model.exception.*;
import com.example.camping.persistence_or_repository.ShoppingCartRepository;
import com.example.camping.service_or_business.PaymentService;
import com.example.camping.service_or_business.ProductService;
import com.example.camping.service_or_business.ShoppingCartService;
import com.example.camping.service_or_business.UserService;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final UserService userService;
    private final ProductService productService;
    private final PaymentService paymentService;

    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartServiceImpl(UserService userService,
                                   ProductService productService,
                                   PaymentService paymentService,
                                   ShoppingCartRepository shoppingCartRepository) {
        this.userService = userService;
        this.productService = productService;
        this.paymentService = paymentService;
        this.shoppingCartRepository = shoppingCartRepository;
    }


    @Override
    public ShoppingCart findActiveShoppingCartByUsername(String userId) {
        return this.shoppingCartRepository.findByUserUsernameAndStatus(userId, CartStatus.CREATED)
                .orElseThrow(() -> new ShoppingCartIsNotActiveException(userId));
    }

    @Override
    public List<ShoppingCart> findAllByUsername(String userId) {
        return this.shoppingCartRepository.findAllByUserUsername(userId);
    }

    @Override
    public ShoppingCart createNewShoppingCart(String userId) {
        User user = this.userService.findById(userId);
        if (this.shoppingCartRepository.existsByUserUsernameAndStatus(
                user.getUsername(),
                CartStatus.CREATED
        )) {
            throw new ShoppingCartIsAlreadyCreated(userId);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return this.shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCart addProductToShoppingCart(String userId, Long productId) {
        ShoppingCart shoppingCart = this.getActiveShoppingCart(userId);
        Product product = this.productService.findById(productId);
        for (Product p : shoppingCart.getProducts()) {
            if (p.getId().equals(productId)) {
                throw new ProductIsAlreadyInShoppingCartException(product.getName());
            }
        }
        shoppingCart.getProducts().add(product);
        return this.shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCart removeProductFromShoppingCart(String userId, Long productId) {
        ShoppingCart shoppingCart = this.getActiveShoppingCart(userId);
        shoppingCart.setProducts(
                shoppingCart.getProducts()
                        .stream()
                        .filter(product -> !product.getId().equals(productId))
                        .collect(Collectors.toList())
        );
        return this.shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart getActiveShoppingCart(String userId) {
        return this.shoppingCartRepository
                .findByUserUsernameAndStatus(userId, CartStatus.CREATED)
                .orElseGet(() -> {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    User user = this.userService.findById(userId);
                    shoppingCart.setUser(user);
                    return this.shoppingCartRepository.save(shoppingCart);
                });
    }

    @Override
    public ShoppingCart cancelActiveShoppingCart(String userId) {
        ShoppingCart shoppingCart = this.shoppingCartRepository
                .findByUserUsernameAndStatus(userId, CartStatus.CREATED)
                .orElseThrow(() -> new ShoppingCartIsNotActiveException(userId));
        shoppingCart.setStatus(CartStatus.CANCELED);
        return this.shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCart checkoutShoppingCart(String userId, ChargeRequest chargeRequest) {
        ShoppingCart shoppingCart = this.shoppingCartRepository
                .findByUserUsernameAndStatus(userId, CartStatus.CREATED)
                .orElseThrow(() -> new ShoppingCartIsNotActiveException(userId));

        List<Product> products = shoppingCart.getProducts();
        float price = 0;

        for (Product product : products) {
            if (product.getQuantity() <= 0) {
                throw new ProductOutOfStockException(product.getName());
            }
            product.setQuantity(product.getQuantity() - 1);
            price += product.getPrice();
        }
        Charge charge = null;
        try {
            charge = this.paymentService.pay(chargeRequest);
        } catch (CardException | APIException | AuthenticationException | APIConnectionException | InvalidRequestException e) {
            throw new TransactionFailedException(userId, e.getMessage());
        }

        shoppingCart.setProducts(products);
        shoppingCart.setStatus(CartStatus.FINISHED);
        return this.shoppingCartRepository.save(shoppingCart);
    }

}
