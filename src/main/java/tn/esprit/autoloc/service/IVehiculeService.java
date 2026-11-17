package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

import java.util.List;

public interface IVehiculeService {

    // ─── Atelier 7 : Méthodes DTO (Section 3.1 & 3.4) ─────────────────────────
    VehiculeDTO creer(VehiculeDTO dto);
    VehiculeDTO obtenirParId(Long id);
    List<VehiculeDTO> listerTous();

    // ─── Atelier 7 : Affectation et désaffectation (Section 1.2 & 1.3) ─────────
    void affecterVehiculeAAgence(Long vehiculeId, Long agenceId);
    void desaffecterVehiculeDeAgence(Long vehiculeId);

    // ─── Méthodes héritées Atelier 4 (compatibilité tests) ───────────────────
    Vehicule create(Vehicule entite);
    Vehicule findById(Long id);
    List<Vehicule> findAll();
    Vehicule update(Long id, Vehicule entite);
    void deleteById(Long id);
}
