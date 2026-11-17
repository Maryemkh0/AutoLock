package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Vehicule;

public interface IVehiculeService {
    Vehicule create(Vehicule entite);
    Vehicule findById(Long id);
    List<Vehicule> findAll();
    Vehicule update(Long id, Vehicule entite);
    void deleteById(Long id);

    /** Atelier 7 - Affectation d'un véhicule à une agence */
    void affecterVehiculeAAgence(Long vehiculeId, Long agenceId);

    /** Atelier 7 - Désaffectation d'un véhicule d'une agence */
    void desaffecterVehiculeDeAgence(Long vehiculeId);
}
