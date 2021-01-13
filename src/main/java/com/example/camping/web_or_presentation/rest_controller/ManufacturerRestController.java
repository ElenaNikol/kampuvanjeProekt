package com.example.camping.web_or_presentation.rest_controller;

import com.example.camping.model.Manufacturer;
import com.example.camping.service_or_business.ManufacturerService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

//@Controller
//@ResponseBody
@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerRestController {

    private final ManufacturerService manufacturerService;

    public ManufacturerRestController(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    public List<Manufacturer> findAll(@RequestParam(required = false) String name) {
        if (name == null) {
            return this.manufacturerService.findAll();
        } else {
            return this.manufacturerService.findAllByName(name);
        }
    }

    @GetMapping("/by-name")
    public List<Manufacturer> findAllByName(@RequestParam String name) {
        return this.manufacturerService.findAllByName(name);
    }



    @GetMapping("/{id}")
    public Manufacturer findById(@PathVariable Long id) {
        return this.manufacturerService.findById(id);
    }

    @PostMapping
    public Manufacturer save(@Valid Manufacturer manufacturer) {
            return this.manufacturerService.save(manufacturer);
    }


    @PutMapping("/{id}")
    public Manufacturer update(@PathVariable Long id, @Valid Manufacturer manufacturer) {
        return this.manufacturerService.update(id, manufacturer);
    }

    @PatchMapping("/{id}")
    public Manufacturer updateName(@PathVariable Long id, @RequestParam String name) {
        return this.manufacturerService.updateName(id, name);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        this.manufacturerService.deleteById(id);
    }


}
