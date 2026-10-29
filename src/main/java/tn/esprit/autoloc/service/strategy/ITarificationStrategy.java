package tn.esprit.autoloc.service.strategy;

import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;

/**
 * Atelier 6 - Patron GoF Strategy pour le calcul dynamique du tarif de location.
 */
public interface ITarificationStrategy {
    BigDecimal calculerTarif(Vehicule vehicule, Reservation reservation);
    String getCode(); // Utilisé pour sélectionner dynamiquement la stratégie
}
