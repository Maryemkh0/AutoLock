package com.example.autoloc.repository;

import com.example.autoloc.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IClientRepository extends JpaRepository<Client, Long> {
}