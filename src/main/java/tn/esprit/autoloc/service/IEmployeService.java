package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.web.dto.EmployeDTO;

import java.util.List;

public interface IEmployeService {

    // ─── Atelier 7 : Méthodes DTO (Section 3.1 & 3.4) ─────────────────────────
    EmployeDTO creer(EmployeDTO dto);
    EmployeDTO obtenirParId(Long id);
    List<EmployeDTO> listerTous();

    // ─── Atelier 7 : Affectation et désaffectation (Section 1.4) ──────────────
    void affecterEmployeAAgence(Long employeId, Long agenceId);
    void desaffecterEmployeDeAgence(Long employeId);

    // ─── Méthodes héritées Atelier 4 (compatibilité tests) ───────────────────
    Employe create(Employe entite);
    Employe findById(Long id);
    List<Employe> findAll();
    Employe update(Long id, Employe entite);
    void deleteById(Long id);
}
