package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.enums.StatutReservation;
import tn.esprit.autoloc.service.observer.ReservationSubject;

import java.time.LocalDate;

/**
 * Atelier 6 - Service métier illustrant l'intégration du patron Observer (ReservationSubject)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationSubject reservationSubject;

    /**
     * Crée une réservation et notifie tous les observateurs (Email, SMS)
     */
    public Reservation creerReservation(Client client) {
        Reservation reservation = Reservation.builder()
                .client(client)
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now().plusDays(2))
                .statut(StatutReservation.CONFIRMEE)
                .build();

        // Déclenche le process métier via le pattern Observer
        reservationSubject.notifierConfirmation(reservation);
        return reservation;
    }

    /**
     * Annule une réservation existante et notifie tous les observateurs (Email, SMS)
     */
    public Reservation annulerReservation(Reservation reservation) {
        reservation.setStatut(StatutReservation.ANNULEE);
        log.info("Reservation #{} annulée pour le client {}",
                reservation.getId(),
                (reservation.getClient() != null ? reservation.getClient().getEmail() : "inconnu"));

        reservationSubject.notifierAnnulation(reservation);
        return reservation;
    }
}
