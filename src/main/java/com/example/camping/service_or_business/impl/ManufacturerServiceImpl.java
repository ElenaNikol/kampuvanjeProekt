package com.example.camping.service_or_business.impl;

import com.example.camping.model.Manufacturer;
import com.example.camping.model.exception.ManufacturerNotFoundException;
import com.example.camping.persistence_or_repository.ManufacturerRepository;
import com.example.camping.service_or_business.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ManufacturerServiceImpl implements ManufacturerService {


    private final ManufacturerRepository manufacturerRepository;

    @Autowired
    public ManufacturerServiceImpl(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    @Override
    public List<Manufacturer> findAll() {
        return this.manufacturerRepository.findAll();
    }

    @Override
    public List<Manufacturer> findAllByName(String name) {
       return this.manufacturerRepository.findAllByName(name);
    }

    @Override
    public Manufacturer findById(Long id) {
        return this.manufacturerRepository.findById(id)
                .orElseThrow(() -> new ManufacturerNotFoundException(id));
    }

    @Override
    public Manufacturer save(Manufacturer manufacturer) {
        return this.manufacturerRepository.save(manufacturer);
    }

    @Override
    public Manufacturer update(Long id, Manufacturer manufacturer) {
        Manufacturer m = this.findById(id);
        m.setName(manufacturer.getName());
        m.setAddress(manufacturer.getAddress());
        return this.manufacturerRepository.save(m);
    }

    @Override
    public Manufacturer updateName(Long id, String name) {
        Manufacturer m = this.findById(id);
        m.setName(name);
        return this.manufacturerRepository.save(m);
    }

    @Override
    public void deleteById(Long id) {
        this.manufacturerRepository.deleteById(id);
    }
}
