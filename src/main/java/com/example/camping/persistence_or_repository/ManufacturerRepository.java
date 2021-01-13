package com.example.camping.persistence_or_repository;

import com.example.camping.model.Manufacturer;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!in-memory")
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
      List<Manufacturer> findAllByName(String name);
}
