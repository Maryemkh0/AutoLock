package com.example.autoloc.repository;

import com.example.autoloc.domain.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVehiculeRepository extends JpaRepository<Vehicule, Long> {
}