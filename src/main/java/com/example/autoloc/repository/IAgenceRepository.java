package com.example.autoloc.repository;

import com.example.autoloc.domain.Agence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAgenceRepository extends JpaRepository<Agence, Long> {
}