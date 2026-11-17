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
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;

import java.util.List;

/**
 * Contrôleur REST Contrats (Atelier 7 - Section 3.4)
 * Utilise ContratDTO pour toutes les entrées et sorties.
 */
@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
@Tag(name = "Contrats", description = "Gestion des contrats de location")
public class ContratController {

    private final IContratService contratService;
    private final IReservationRepository reservationRepository;
    private final ContratMapper contratMapper;

    @Operation(summary = "Lister tous les contrats")
    @ApiResponse(responseCode = "200", description = "Liste des contrats récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<ContratDTO>> findAll() {
        List<ContratDTO> dtos = contratService.findAll().stream()
                .map(contratMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consulter un contrat par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContratDTO> findById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        return ResponseEntity.ok(contratMapper.toDto(contratService.findById(id)));
    }

    @Operation(summary = "Créer un nouveau contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou identifiant déjà renseigné")
    })
    @PostMapping
    public ResponseEntity<ContratDTO> create(@Valid @RequestBody ContratDTO dto) {
        Contrat contrat = contratMapper.toEntity(dto);
        if (dto.getReservationId() != null) {
            contrat.setReservation(reservationRepository.findById(dto.getReservationId()).orElse(null));
        }
        Contrat saved = contratService.create(contrat);
        return ResponseEntity.status(HttpStatus.CREATED).body(contratMapper.toDto(saved));
    }

    @Operation(summary = "Modifier un contrat existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ContratDTO> update(@Parameter(description = "Identifiant du contrat") @PathVariable Long id,
                                             @Valid @RequestBody ContratDTO dto) {
        Contrat existant = contratService.findById(id);
        existant.setDateSignature(dto.getDateSignature());
        existant.setMontantTotal(dto.getMontantTotal());
        existant.setValide(dto.isValide());
        if (dto.getReservationId() != null) {
            existant.setReservation(reservationRepository.findById(dto.getReservationId()).orElse(null));
        }
        Contrat updated = contratService.update(id, existant);
        return ResponseEntity.ok(contratMapper.toDto(updated));
    }

    @Operation(summary = "Supprimer un contrat par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrat supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        contratService.deleteById(id);
    }
}
