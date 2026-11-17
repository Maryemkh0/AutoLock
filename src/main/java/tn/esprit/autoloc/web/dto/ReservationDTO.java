package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import tn.esprit.autoloc.domain.enums.StatutReservation;

import java.time.LocalDate;

/**
 * DTO pour la création/modification d'une réservation.
 */
@Data
public class ReservationDTO {

    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    private StatutReservation statut;

    @NotNull(message = "L'identifiant du client est obligatoire")
    @Positive(message = "L'identifiant du client doit être positif")
    private Long idClient;

    @NotNull(message = "L'identifiant du véhicule est obligatoire")
    @Positive(message = "L'identifiant du véhicule doit être positif")
    private Long idVehicule;
}
