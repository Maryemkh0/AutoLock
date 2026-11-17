package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import tn.esprit.autoloc.domain.enums.CategorieVehicule;
import tn.esprit.autoloc.domain.enums.StatutVehicule;

import java.math.BigDecimal;

/**
 * DTO pour la création/modification d'un véhicule.
 * Sépare les données de l'API de l'entité JPA.
 * Les annotations Bean Validation remplacent les vérifications manuelles.
 */
@Data
public class VehiculeDTO {

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Pattern(regexp = "[A-Z0-9\\-]{4,10}", message = "Format d'immatriculation invalide (ex: 123TU1234)")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    @Size(min = 2, max = 50, message = "La marque doit avoir entre 2 et 50 caractères")
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(min = 1, max = 50, message = "Le modèle doit avoir entre 1 et 50 caractères")
    private String modele;

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieVehicule categorie;

    @NotNull(message = "Le tarif journalier est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le tarif journalier ne peut pas être négatif")
    private BigDecimal tarifJournalier;

    @PositiveOrZero(message = "Le kilométrage ne peut pas être négatif")
    private Integer kilometrage;

    private StatutVehicule statut;

    /** Identifiant de l'agence à laquelle affecter le véhicule (optionnel) */
    private Long idAgence;
}
