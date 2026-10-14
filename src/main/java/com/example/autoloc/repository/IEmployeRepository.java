package com.example.autoloc.repository;

import com.example.autoloc.domain.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEmployeRepository extends JpaRepository<Employe, Long> {
}