package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.web.dto.ContratDTO;

import java.util.List;

public interface IContratService {

    // ─── Atelier 7 : Méthodes DTO (Section 3.1 & 3.4) ─────────────────────────
    ContratDTO creer(ContratDTO dto);
    ContratDTO obtenirParId(Long id);
    List<ContratDTO> listerTous();

    // ─── Méthodes héritées Atelier 4 (compatibilité tests) ───────────────────
    Contrat create(Contrat entite);
    Contrat findById(Long id);
    List<Contrat> findAll();
    Contrat update(Long id, Contrat entite);
    void deleteById(Long id);
}
