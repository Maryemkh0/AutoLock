package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import tn.esprit.autoloc.domain.enums.RoleEmploye;

/**
 * DTO pour la création/modification d'un employé.
 */
@Data
public class EmployeDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit avoir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit avoir entre 2 et 50 caractères")
    private String prenom;

    @NotNull(message = "Le rôle est obligatoire")
    private RoleEmploye role;

    /** Identifiant de l'agence à laquelle affecter l'employé (optionnel) */
    private Long idAgence;
}
