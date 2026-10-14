package com.example.autoloc.repository;

import com.example.autoloc.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
}