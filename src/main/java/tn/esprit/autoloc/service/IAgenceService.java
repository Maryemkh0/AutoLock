
package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Agence;

public interface IAgenceService {
    Agence create(Agence entite);
    Agence findById(Long id);
    List<Agence> findAll();
    Agence update(Long id, Agence entite);
    void deleteById(Long id);

    /** Affecte un véhicule existant à une agence existante */
    Agence affecterVehicule(Long idAgence, Long idVehicule);

    /** Retire un véhicule d'une agence */
    Agence retirerVehicule(Long idAgence, Long idVehicule);

    /** Affecte un employé existant à une agence existante */
    Agence affecterEmploye(Long idAgence, Long idEmploye);
}
