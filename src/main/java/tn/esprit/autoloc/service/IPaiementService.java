package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.web.dto.PaiementDTO;

import java.util.List;

public interface IPaiementService {

    // ─── Atelier 7 : Méthodes DTO (Section 3.1 & 3.4) ─────────────────────────
    PaiementDTO creer(PaiementDTO dto);
    PaiementDTO obtenirParId(Long id);
    List<PaiementDTO> listerTous();

    // ─── Méthodes héritées Atelier 4 (compatibilité tests) ───────────────────
    Paiement create(Paiement entite);
    Paiement findById(Long id);
    List<Paiement> findAll();
    Paiement update(Long id, Paiement entite);
    void deleteById(Long id);
}
