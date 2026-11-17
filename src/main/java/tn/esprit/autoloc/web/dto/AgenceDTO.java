package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO pour la création/modification d'une agence.
 */
@Data
public class AgenceDTO {

    @NotBlank(message = "Le nom de l'agence est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit avoir entre 2 et 100 caractères")
    private String nom;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String adresse;

    @Pattern(regexp = "\\+?[0-9\\s\\-]{8,15}", message = "Numéro de téléphone invalide")
    private String telephone;
}
