package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.web.dto.ReservationRequestDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;

import java.util.List;

public interface IReservationService {

    // ─── Atelier 7 : Méthodes DTO (Section 3.1 & 3.4 & 5.5) ───────────────────
    ReservationResponseDTO creer(ReservationRequestDTO dto);
    ReservationResponseDTO obtenirParId(Long id);
    List<ReservationResponseDTO> listerToutes();

    // ─── Méthodes héritées Atelier 4 (compatibilité tests) ───────────────────
    Reservation create(Reservation entite);
    Reservation findById(Long id);
    List<Reservation> findAll();
    Reservation update(Long id, Reservation entite);
    void deleteById(Long id);
}
