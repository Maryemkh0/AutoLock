package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Employe;

public interface IEmployeService {
    Employe create(Employe entite);
    Employe findById(Long id);
    List<Employe> findAll();
    Employe update(Long id, Employe entite);
    void deleteById(Long id);

    /** Atelier 7 - Affectation d'un employé à une agence */
    void affecterEmployeAAgence(Long employeId, Long agenceId);

    /** Atelier 7 - Désaffectation d'un employé d'une agence */
    void desaffecterEmployeDeAgence(Long employeId);
}
