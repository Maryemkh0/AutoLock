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
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.web.dto.AgenceDTO;
import tn.esprit.autoloc.web.mapper.AgenceMapper;

import java.util.List;

/**
 * Contrôleur REST Agences (Atelier 7 - Section 3.3)
 * Utilise AgenceDTO pour toutes les entrées et sorties.
 */
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
    public ResponseEntity<List<AgenceDTO>> findAll() {
        List<AgenceDTO> dtos = agenceService.findAll().stream()
                .map(agenceMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consulter une agence par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence trouvée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AgenceDTO> findById(@Parameter(description = "Identifiant de l'agence") @PathVariable Long id) {
        return ResponseEntity.ok(agenceMapper.toDto(agenceService.findById(id)));
    }

    @Operation(summary = "Créer une nouvelle agence")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agence créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<AgenceDTO> creer(@Valid @RequestBody AgenceDTO dto) {
        Agence agence = agenceMapper.toEntity(dto);
        Agence saved = agenceService.create(agence);
        return ResponseEntity.status(HttpStatus.CREATED).body(agenceMapper.toDto(saved));
    }

    @Operation(summary = "Modifier une agence existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AgenceDTO> update(
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long id,
            @Valid @RequestBody AgenceDTO dto) {
        Agence existante = agenceService.findById(id);
        existante.setNom(dto.getNom());
        existante.setVille(dto.getVille());
        existante.setAdresse(dto.getAdresse());
        existante.setTelephone(dto.getTelephone());
        Agence updated = agenceService.update(id, existante);
        return ResponseEntity.ok(agenceMapper.toDto(updated));
    }

    @Operation(summary = "Supprimer une agence par identifiant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'agence") @PathVariable Long id) {
        agenceService.deleteById(id);
    }
}
