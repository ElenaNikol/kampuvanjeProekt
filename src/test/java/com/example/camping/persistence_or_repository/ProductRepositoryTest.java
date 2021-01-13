package com.example.camping.persistence_or_repository;

import com.example.camping.model.Manufacturer;
import com.example.camping.model.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductRepositoryTest {

    @Autowired
    ManufacturerRepository manufacturerRepository;

    @Autowired
    ProductRepository productRepository;

    @BeforeAll
    public void init() {
        Manufacturer m = new Manufacturer();
        m.setId(1l);
        m.setName("name1");
        m = manufacturerRepository.save(m);

        Product p1,p2,p3;
        p1 = new Product();
        p1.setQuantity(5);
        p1.setPrice(5f);
        p1.setManufacturer(m);

        p2 = new Product();
        p2.setQuantity(2);
        p2.setPrice(2f);
        p2.setManufacturer(m);

        p3 = new Product();
        p3.setQuantity(3);
        p3.setPrice(3f);
        p3.setManufacturer(m);
        this.productRepository.save(p1);
        this.productRepository.save(p2);
        this.productRepository.save(p3);
    }


    @Test
    void findAllByManufacturerId() {
        List<Product> products = this.productRepository.findAllByManufacturerId(1l);
        assert products.size() == 3;
    }

    @Test
    void findAllByNameLikeAndManufacturerIdOrderByPriceDesc() {
    }
}