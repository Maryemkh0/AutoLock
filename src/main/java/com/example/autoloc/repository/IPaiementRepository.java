package com.example.autoloc.repository;

import com.example.autoloc.domain.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPaiementRepository extends JpaRepository<Paiement, Long> {
}