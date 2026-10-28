package tn.esprit.autoloc.repository;

import tn.esprit.autoloc.domain.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMaintenanceRepository extends JpaRepository<Maintenance, Long> {
}