package tn.esprit.autoloc.repository;

import tn.esprit.autoloc.domain.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEquipementRepository extends JpaRepository<Equipement, Long> {
}