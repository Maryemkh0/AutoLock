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
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

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

    @Operation(summary = "Lister tous les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<VehiculeDTO>> findAll() {
        return ResponseEntity.ok(vehiculeService.listerTous());
    }

    @Operation(summary = "Consulter un véhicule par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDTO> findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.obtenirParId(id));
    }

    @Operation(summary = "Créer un nouveau véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<VehiculeDTO> creer(@Valid @RequestBody VehiculeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehiculeService.creer(dto));
    }

    @Operation(summary = "Supprimer un véhicule par identifiant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    // ─── Atelier 7 : Endpoints d'affectation et désaffectation (Section 1.2 & 1.3) ───

    @Operation(summary = "Affecter un véhicule à une agence (PUT)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule affecté avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou agence introuvable")
    })
    @PutMapping(value = {"/{vehiculeId}/affecter-agence/{agenceId}", "/{vehiculeId}/agence/{agenceId}"})
    public ResponseEntity<VehiculeDTO> affecterVehiculeAAgence(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long vehiculeId,
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long agenceId) {
        vehiculeService.affecterVehiculeAAgence(vehiculeId, agenceId);
        return ResponseEntity.ok(vehiculeService.obtenirParId(vehiculeId));
    }

    @Operation(summary = "Désaffecter un véhicule d'une agence (PUT)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule désaffecté avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @PutMapping(value = {"/{vehiculeId}/desaffecter-agence", "/{vehiculeId}/agence/desaffecter"})
    public ResponseEntity<VehiculeDTO> desaffecterVehiculeDeAgence(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long vehiculeId) {
        vehiculeService.desaffecterVehiculeDeAgence(vehiculeId);
        return ResponseEntity.ok(vehiculeService.obtenirParId(vehiculeId));
    }
}
