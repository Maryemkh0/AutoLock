package com.example.autoloc.repository;

import com.example.autoloc.domain.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEquipementRepository extends JpaRepository<Equipement, Long> {
}