package com.example.camping.service_or_business.impl;

import com.example.camping.model.Category;
import com.example.camping.model.Manufacturer;
import com.example.camping.model.Product;
import com.example.camping.model.exception.ProductIsAlreadyInShoppingCartException;
import com.example.camping.model.exception.ProductNotFoundException;
import com.example.camping.persistence_or_repository.ProductRepository;
import com.example.camping.service_or_business.CategoryService;
import com.example.camping.service_or_business.ManufacturerService;
import com.example.camping.service_or_business.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ManufacturerService manufacturerService;
    private final CategoryService categoryService;

    public ProductServiceImpl(ProductRepository productRepository,
                              ManufacturerService manufacturerService,
                              CategoryService categoryService) {
        this.productRepository = productRepository;
        this.manufacturerService = manufacturerService;
        this.categoryService = categoryService;
    }

    @Override
    public List<Product> findAll() {
        return this.productRepository.findAll();
    }

    @Override
    public List<Product> findAllByManufacturerId(Long manufacturerId) {
        return this.productRepository.findAllByManufacturerId(manufacturerId);
    }

    @Override
    public List<Product> findAllByCategoryId(Long categoryId) {
        return this.productRepository.findAllByCategoryId(categoryId);
    }


    @Override
    public Product findById(Long id) {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product saveProduct(String name, Float price, Integer quantity, Long manufacturerId, Long categoryId) {
        Manufacturer manufacturer = this.manufacturerService.findById(manufacturerId);
        Category category = this.categoryService.findById(categoryId);
        Product product = new Product(null, name, price, quantity, manufacturer, category);
        return this.productRepository.save(product);
    }

    @Override
    public Product saveProduct(Product product, MultipartFile image) throws IOException {
        Manufacturer manufacturer = this.manufacturerService.findById(product.getManufacturer().getId());
        Category category = this.categoryService.findById(product.getCategory().getId());
        product.setManufacturer(manufacturer);
        product.setCategory(category);
        if (image != null && !image.getName().isEmpty()) {
            byte[] bytes = image.getBytes();
            String base64Image = String.format("data:%s;base64,%s", image.getContentType(), Base64.getEncoder().encodeToString(bytes));
            product.setImageBase64(base64Image);
        }
        return this.productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product, MultipartFile image) throws IOException {
        Product p = this.findById(id);
        Manufacturer manufacturer = this.manufacturerService.findById(product.getManufacturer().getId());
        p.setManufacturer(manufacturer);
        Category category = this.categoryService.findById(product.getCategory().getId());
        product.setCategory(category);
        p.setPrice(product.getPrice());
        p.setQuantity(product.getQuantity());
        if (image != null && !image.getName().isEmpty()) {
            byte[] bytes = image.getBytes();
            String base64Image = String.format("data:%s;base64,%s", image.getContentType(), Base64.getEncoder().encodeToString(bytes));
            p.setImageBase64(base64Image);
        }
        return this.productRepository.save(p);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Product product = this.findById(id);
        if (product.getShoppingCarts().size() > 0) {
            throw new ProductIsAlreadyInShoppingCartException(product.getName());
        }
        this.productRepository.deleteById(id);
    }
}
