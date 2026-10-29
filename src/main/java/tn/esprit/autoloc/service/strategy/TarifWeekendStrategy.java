package tn.esprit.autoloc.service.strategy;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Component
public class TarifWeekendStrategy implements ITarificationStrategy {

    private static final BigDecimal REMISE_WEEKEND = new BigDecimal("0.90");

    @Override
    public BigDecimal calculerTarif(Vehicule vehicule, Reservation reservation) {
        long jours = calculerNombreJours(reservation);
        BigDecimal tarifJournalier = (vehicule != null && vehicule.getTarifJournalier() != null)
                ? vehicule.getTarifJournalier()
                : new BigDecimal("180.00");
        return tarifJournalier.multiply(BigDecimal.valueOf(jours)).multiply(REMISE_WEEKEND);
    }

    @Override
    public String getCode() {
        return "WEEKEND";
    }

    private long calculerNombreJours(Reservation reservation) {
        if (reservation != null && reservation.getDateDebut() != null && reservation.getDateFin() != null) {
            long jours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
            return jours > 0 ? jours : 1;
        }
        return 1;
    }
}
