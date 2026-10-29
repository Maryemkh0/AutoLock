package tn.esprit.autoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.domain.enums.StatutReservation;
import tn.esprit.autoloc.service.ReservationService;
import tn.esprit.autoloc.service.factory.NotificationFactory;
import tn.esprit.autoloc.service.factory.TarificationStrategyFactory;
import tn.esprit.autoloc.service.observer.IReservationObserver;
import tn.esprit.autoloc.service.observer.ReservationSubject;
import tn.esprit.autoloc.service.strategy.ITarificationStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationSubject reservationSubject;

    @Autowired
    private TarificationStrategyFactory tarificationStrategyFactory;

    @Autowired
    private NotificationFactory notificationFactory;

    @Test
    void testCreerEtNotifierReservation() {
        System.out.println("=== TEST OBSERVER PATTERN : CREATION ET CONFIRMATION ===");

        // 1. Préparer le client
        Client client = new Client();
        client.setIdClient(100L);
        client.setNom("Ben Salem");
        client.setPrenom("Mehdi");
        client.setEmail("mehdi.bensalem@esprit.tn");
        client.setTelephone("+216 98 123 456");

        // 2. Créer une réservation via ReservationService (déclenche le Subject Observer)
        Reservation reservation = reservationService.creerReservation(client);
        reservation.setIdReservation(101L);

        assertNotNull(reservation);
        assertEquals(StatutReservation.CONFIRMEE, reservation.getStatut());
        assertEquals("mehdi.bensalem@esprit.tn", reservation.getClient().getEmail());

        System.out.println("Réservation créée avec succès : ID=" + reservation.getId());
        System.out.println("=== FIN TEST CREATION ===\n");
    }

    @Test
    void testAnnulerEtNotifierReservation() {
        System.out.println("=== TEST OBSERVER PATTERN : ANNULATION ===");

        Client client = new Client();
        client.setIdClient(100L);
        client.setEmail("mehdi.bensalem@esprit.tn");
        client.setTelephone("+216 98 123 456");

        Reservation reservation = Reservation.builder()
                .idReservation(202L)
                .client(client)
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now().plusDays(3))
                .statut(StatutReservation.CONFIRMEE)
                .build();

        // 3. Annuler la réservation (déclenche notifierAnnulation)
        Reservation annulee = reservationService.annulerReservation(reservation);

        assertNotNull(annulee);
        assertEquals(StatutReservation.ANNULEE, annulee.getStatut());

        System.out.println("Réservation annulée : ID=" + annulee.getId() + ", Statut=" + annulee.getStatut());
        System.out.println("=== FIN TEST ANNULATION ===\n");
    }

    @Test
    void testStrategyPatternEtFactory() {
        System.out.println("=== TEST STRATEGY PATTERN & FACTORY METHOD ===");

        Vehicule vehicule = new Vehicule();
        vehicule.setTarifJournalier(new BigDecimal("100.00"));

        // 1. Test séjour standard (3 jours en semaine)
        Reservation resStandard = Reservation.builder()
                .dateDebut(LocalDate.of(2026, 11, 2)) // Lundi
                .dateFin(LocalDate.of(2026, 11, 5))   // Jeudi
                .build();
        ITarificationStrategy stratStandard = tarificationStrategyFactory.resoudre(resStandard);
        assertEquals("STANDARD", stratStandard.getCode());
        BigDecimal tarifStandard = stratStandard.calculerTarif(vehicule, resStandard);
        assertEquals(0, new BigDecimal("300.00").compareTo(tarifStandard));
        System.out.println("Tarif Standard (3 jours x 100) : " + tarifStandard);

        // 2. Test séjour longue durée (10 jours -> remise 20%)
        Reservation resLongue = Reservation.builder()
                .dateDebut(LocalDate.of(2026, 11, 1))
                .dateFin(LocalDate.of(2026, 11, 11)) // 10 jours
                .build();
        ITarificationStrategy stratLongue = tarificationStrategyFactory.resoudre(resLongue);
        assertEquals("LONGUE_DUREE", stratLongue.getCode());
        BigDecimal tarifLongue = stratLongue.calculerTarif(vehicule, resLongue);
        // 10 jours * 100 * 0.80 = 800.00
        assertEquals(0, new BigDecimal("800.00").compareTo(tarifLongue));
        System.out.println("Tarif Longue Durée (10 jours x 100 x 0.8) : " + tarifLongue);

        // 3. Test NotificationFactory
        IReservationObserver emailObs = notificationFactory.creerObservateur("EMAIL");
        assertNotNull(emailObs);
        IReservationObserver smsObs = notificationFactory.creerObservateur("SMS");
        assertNotNull(smsObs);
        assertThrows(IllegalArgumentException.class, () -> notificationFactory.creerObservateur("PIGEON"));

        System.out.println("=== FIN TEST STRATEGY & FACTORY ===\n");
    }
}
