package com.example.autoloc.repository;

import com.example.autoloc.domain.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
}