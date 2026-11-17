package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion du parc de véhicules")
public class VehiculeController {

    private final IVehiculeService vehiculeService;
    private final IAgenceService agenceService;
    private final VehiculeMapper vehiculeMapper;

    @Operation(summary = "Lister tous les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée avec succès")
    @GetMapping
    public List<VehiculeDTO> findAll() {
        return vehiculeService.findAll()
                .stream()
                .map(vehiculeMapper::toDto)
                .toList();
    }

    @Operation(summary = "Consulter un véhicule par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/{id}")
    public VehiculeDTO findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return vehiculeMapper.toDto(vehiculeService.findById(id));
    }

    @Operation(summary = "Créer un nouveau véhicule",
               description = "Toutes les contraintes de validation sont appliquées automatiquement")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeDTO create(@Valid @RequestBody VehiculeDTO dto) {
        Vehicule vehicule = vehiculeMapper.toEntity(dto);

        // Affectation de l'agence si fournie dans le DTO
        if (dto.getIdAgence() != null) {
            Agence agence = agenceService.findById(dto.getIdAgence());
            vehicule.setAgence(agence);
        }

        return vehiculeMapper.toDto(vehiculeService.create(vehicule));
    }

    @Operation(summary = "Modifier un véhicule existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @PutMapping("/{id}")
    public VehiculeDTO update(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long id,
            @Valid @RequestBody VehiculeDTO dto) {

        Vehicule existant = vehiculeService.findById(id);
        vehiculeMapper.updateEntityFromDto(dto, existant);

        // Affectation ou retrait de l'agence
        if (dto.getIdAgence() != null) {
            Agence agence = agenceService.findById(dto.getIdAgence());
            existant.setAgence(agence);
        }

        return vehiculeMapper.toDto(vehiculeService.update(id, existant));
    }

    @Operation(summary = "Supprimer un véhicule par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }
}
