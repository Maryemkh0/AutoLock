package tn.esprit.autoloc.service.factory;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.service.strategy.ITarificationStrategy;

import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Selectionne dynamiquement la TarificationStrategy adaptee a une reservation
 * (duree du sejour). Toutes les implementations Spring de ITarificationStrategy
 * sont injectees automatiquement (Map<code, strategy>).
 */
@Component
public class TarificationStrategyFactory {

    private final Map<String, ITarificationStrategy> strategies = new HashMap<>();

    public TarificationStrategyFactory(List<ITarificationStrategy> strategyList) {
        for (ITarificationStrategy strategy : strategyList) {
            strategies.put(strategy.getCode(), strategy);
        }
    }

    public ITarificationStrategy resoudre(Reservation reservation) {
        if (reservation == null || reservation.getDateDebut() == null || reservation.getDateFin() == null) {
            return strategies.get("STANDARD");
        }

        long duree = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
        if (duree >= 7) {
            return strategies.get("LONGUE_DUREE");
        } else if (estWeekend(reservation)) {
            return strategies.get("WEEKEND");
        }
        return strategies.get("STANDARD");
    }

    private boolean estWeekend(Reservation reservation) {
        DayOfWeek debut = reservation.getDateDebut().getDayOfWeek();
        DayOfWeek fin = reservation.getDateFin().getDayOfWeek();
        return (debut .equals(DayOfWeek.FRIDAY)|| debut .equals(DayOfWeek.SATURDAY)|| debut .equals(DayOfWeek.SUNDAY))
                && (fin .equals(DayOfWeek.SATURDAY)|| fin .equals(DayOfWeek.SUNDAY)|| fin .equals(DayOfWeek.MONDAY));
    }
}
