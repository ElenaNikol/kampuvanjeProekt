package com.example.camping.service_or_business.impl;


import com.example.camping.model.Category;
import com.example.camping.model.exception.CategoryNotFoundException;
import com.example.camping.persistence_or_repository.CategoryRepository;
import com.example.camping.service_or_business.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return this.categoryRepository.findAll();
    }

    @Override
    public List<Category> findAllByName(String name) {
        return categoryRepository.findAllByName(name);
    }


    @Override
    public Category findById(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public Category save(Category manufacturer) {
        return this.categoryRepository.save(manufacturer);
    }

    @Override
    public Category update(Long id, Category manufacturer) {
        Category m = this.findById(id);
        m.setName(manufacturer.getName());
        return this.categoryRepository.save(m);
    }

    @Override
    public Category updateName(Long id, String name) {
        Category m = this.findById(id);
        m.setName(name);
        return this.categoryRepository.save(m);
    }

    @Override
    public void deleteById(Long id) {
        this.categoryRepository.deleteById(id);
    }
}