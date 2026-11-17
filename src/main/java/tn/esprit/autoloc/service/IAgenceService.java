package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.web.dto.AgenceDTO;

import java.util.List;

public interface IAgenceService {

    // ─── Atelier 7 : Méthodes DTO (Section 3.1) ──────────────────────────────
    AgenceDTO creer(AgenceDTO dto);
    AgenceDTO obtenirParId(Long id);
    List<AgenceDTO> listerToutes();

    // ─── Méthodes héritées Atelier 4 (compatibilité tests) ───────────────────
    Agence create(Agence entite);
    Agence findById(Long id);
    List<Agence> findAll();
    Agence update(Long id, Agence entite);
    void deleteById(Long id);
}
