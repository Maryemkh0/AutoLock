package tn.esprit.autoloc.service.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Reservation;

/**
 * Observateur concret : notification par SMS (simulée via logs).
 */
@Slf4j
@Component
public class SmsNotificationObserver implements IReservationObserver {

    @Override
    public void onReservationConfirmee(Reservation reservation) {
        String telephone = (reservation != null && reservation.getClient() != null)
                ? reservation.getClient().getTelephone()
                : "inconnu";
        Long id = (reservation != null) ? reservation.getId() : null;
        log.info("[SMS] Confirmation envoyee au {} pour la reservation #{}", telephone, id);
    }

    @Override
    public void onReservationAnnulee(Reservation reservation) {
        String telephone = (reservation != null && reservation.getClient() != null)
                ? reservation.getClient().getTelephone()
                : "inconnu";
        Long id = (reservation != null) ? reservation.getId() : null;
        log.info("[SMS] Annulation notifiee au {} pour la reservation #{}", telephone, id);
    }
}
