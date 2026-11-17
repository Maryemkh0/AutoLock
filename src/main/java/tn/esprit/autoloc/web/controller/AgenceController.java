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
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.web.dto.AgenceDTO;
import tn.esprit.autoloc.web.mapper.AgenceMapper;

import java.util.List;

@RestController
@RequestMapping("/agences")
@RequiredArgsConstructor
@Tag(name = "Agences", description = "Gestion des agences AutoLoc")
public class AgenceController {

    private final IAgenceService agenceService;
    private final AgenceMapper agenceMapper;

    @Operation(summary = "Lister toutes les agences")
    @ApiResponse(responseCode = "200", description = "Liste des agences récupérée avec succès")
    @GetMapping
    public List<AgenceDTO> findAll() {
        return agenceService.findAll()
                .stream()
                .map(agenceMapper::toDto)
                .toList();
    }

    @Operation(summary = "Consulter une agence par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence trouvée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @GetMapping("/{id}")
    public AgenceDTO findById(@Parameter(description = "Identifiant de l'agence") @PathVariable Long id) {
        return agenceMapper.toDto(agenceService.findById(id));
    }

    @Operation(summary = "Créer une nouvelle agence")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agence créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgenceDTO create(@Valid @RequestBody AgenceDTO dto) {
        Agence agence = agenceMapper.toEntity(dto);
        return agenceMapper.toDto(agenceService.create(agence));
    }

    @Operation(summary = "Modifier une agence existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @PutMapping("/{id}")
    public AgenceDTO update(
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long id,
            @Valid @RequestBody AgenceDTO dto) {
        Agence agence = agenceMapper.toEntity(dto);
        return agenceMapper.toDto(agenceService.update(id, agence));
    }

    @Operation(summary = "Supprimer une agence par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agence supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'agence") @PathVariable Long id) {
        agenceService.deleteById(id);
    }

    // ─── Endpoints d'affectation (Atelier 7) ─────────────────────────────────

    @Operation(summary = "Affecter un véhicule à une agence",
               description = "Lie un véhicule existant à une agence existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule affecté avec succès"),
            @ApiResponse(responseCode = "404", description = "Agence ou véhicule introuvable")
    })
    @PutMapping("/{idAgence}/vehicules/{idVehicule}")
    public AgenceDTO affecterVehicule(
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence,
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule) {
        return agenceMapper.toDto(agenceService.affecterVehicule(idAgence, idVehicule));
    }

    @Operation(summary = "Retirer un véhicule d'une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule retiré avec succès"),
            @ApiResponse(responseCode = "400", description = "Le véhicule n'appartient pas à cette agence"),
            @ApiResponse(responseCode = "404", description = "Agence ou véhicule introuvable")
    })
    @DeleteMapping("/{idAgence}/vehicules/{idVehicule}")
    public AgenceDTO retirerVehicule(
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence,
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule) {
        return agenceMapper.toDto(agenceService.retirerVehicule(idAgence, idVehicule));
    }

    @Operation(summary = "Affecter un employé à une agence",
               description = "Lie un employé existant à une agence existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé affecté avec succès"),
            @ApiResponse(responseCode = "404", description = "Agence ou employé introuvable")
    })
    @PutMapping("/{idAgence}/employes/{idEmploye}")
    public AgenceDTO affecterEmploye(
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence,
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long idEmploye) {
        return agenceMapper.toDto(agenceService.affecterEmploye(idAgence, idEmploye));
    }
}
