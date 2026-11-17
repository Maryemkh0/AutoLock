package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IPaiementService;
import tn.esprit.autoloc.web.dto.PaiementDTO;
import tn.esprit.autoloc.web.mapper.PaiementMapper;

import java.util.List;

/**
 * Contrôleur REST Paiements (Atelier 7 - Section 3.4)
 * Utilise PaiementDTO pour toutes les sorties.
 */
@RestController
@RequestMapping("/paiements")
@RequiredArgsConstructor
@Tag(name = "Paiements", description = "Consultation des paiements (lecture seule)")
public class PaiementController {

    private final IPaiementService paiementService;
    private final PaiementMapper paiementMapper;

    @Operation(summary = "Lister tous les paiements")
    @ApiResponse(responseCode = "200", description = "Liste des paiements récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<PaiementDTO>> findAll() {
        List<PaiementDTO> dtos = paiementService.findAll().stream()
                .map(paiementMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consulter un paiement par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement trouvé"),
            @ApiResponse(responseCode = "404", description = "Paiement introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaiementDTO> findById(@Parameter(description = "Identifiant du paiement") @PathVariable Long id) {
        return ResponseEntity.ok(paiementMapper.toDto(paiementService.findById(id)));
    }
}
