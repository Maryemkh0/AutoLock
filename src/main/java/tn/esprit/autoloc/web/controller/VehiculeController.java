package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

import java.util.List;

/**
 * Contrôleur REST Véhicules (Atelier 7 - Section 1.2, 1.3, 3.3)
 * Utilise VehiculeDTO et expose les opérations d'affectation/désaffectation.
 */
@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion du parc de véhicules")
public class VehiculeController {

    private final IVehiculeService vehiculeService;
    private final IAgenceRepository agenceRepository;
    private final VehiculeMapper vehiculeMapper;

    @Operation(summary = "Lister tous les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<VehiculeDTO>> findAll() {
        List<VehiculeDTO> dtos = vehiculeService.findAll().stream()
                .map(vehiculeMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consulter un véhicule par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDTO> findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return ResponseEntity.ok(vehiculeMapper.toDto(vehiculeService.findById(id)));
    }

    @Operation(summary = "Créer un nouveau véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<VehiculeDTO> create(@Valid @RequestBody VehiculeDTO dto) {
        Vehicule vehicule = vehiculeMapper.toEntity(dto);
        if (dto.getAgenceId() != null) {
            Agence agence = agenceRepository.findById(dto.getAgenceId()).orElse(null);
            vehicule.setAgence(agence);
        }
        Vehicule saved = vehiculeService.create(vehicule);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeMapper.toDto(saved));
    }

    @Operation(summary = "Modifier un véhicule existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDTO> update(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long id,
            @Valid @RequestBody VehiculeDTO dto) {
        Vehicule existant = vehiculeService.findById(id);
        existant.setImmatriculation(dto.getImmatriculation());
        existant.setMarque(dto.getMarque());
        existant.setModele(dto.getModele());
        existant.setCategorie(dto.getCategorie());
        existant.setTarifJournalier(dto.getTarifJournalier());
        if (dto.getKilometrage() != null) {
            existant.setKilometrage(dto.getKilometrage());
        }
        if (dto.getStatut() != null) {
            existant.setStatut(dto.getStatut());
        }
        if (dto.getAgenceId() != null) {
            existant.setAgence(agenceRepository.findById(dto.getAgenceId()).orElse(null));
        }

        Vehicule updated = vehiculeService.update(id, existant);
        return ResponseEntity.ok(vehiculeMapper.toDto(updated));
    }

    @Operation(summary = "Supprimer un véhicule par identifiant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    // ─── Atelier 7 : Endpoints d'affectation et désaffectation (Section 1.2 & 1.3) ───

    @Operation(summary = "Affecter un véhicule à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule affecté avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou agence introuvable")
    })
    @PutMapping("/{vehiculeId}/affecter-agence/{agenceId}")
    public ResponseEntity<VehiculeDTO> affecterVehiculeAAgence(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long vehiculeId,
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long agenceId) {
        vehiculeService.affecterVehiculeAAgence(vehiculeId, agenceId);
        Vehicule v = vehiculeService.findById(vehiculeId);
        return ResponseEntity.ok(vehiculeMapper.toDto(v));
    }

    @Operation(summary = "Désaffecter un véhicule d'une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule désaffecté avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @PutMapping("/{vehiculeId}/desaffecter-agence")
    public ResponseEntity<VehiculeDTO> desaffecterVehiculeDeAgence(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long vehiculeId) {
        vehiculeService.desaffecterVehiculeDeAgence(vehiculeId);
        Vehicule v = vehiculeService.findById(vehiculeId);
        return ResponseEntity.ok(vehiculeMapper.toDto(v));
    }
}
