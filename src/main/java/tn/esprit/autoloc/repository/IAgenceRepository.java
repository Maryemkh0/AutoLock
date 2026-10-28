package tn.esprit.autoloc.repository;

import tn.esprit.autoloc.domain.Agence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAgenceRepository extends JpaRepository<Agence, Long> {
}