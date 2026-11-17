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
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.web.dto.EmployeDTO;
import tn.esprit.autoloc.web.mapper.EmployeMapper;

import java.util.List;

/**
 * Contrôleur REST Employés (Atelier 7 - Section 1.4, 3.3)
 * Utilise EmployeDTO et expose les opérations d'affectation/désaffectation.
 */
@RestController
@RequestMapping("/employes")
@RequiredArgsConstructor
@Tag(name = "Employés", description = "Gestion des employés AutoLoc")
public class EmployeController {

    private final IEmployeService employeService;
    private final IAgenceRepository agenceRepository;
    private final EmployeMapper employeMapper;

    @Operation(summary = "Lister tous les employés")
    @ApiResponse(responseCode = "200", description = "Liste des employés récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<EmployeDTO>> findAll() {
        List<EmployeDTO> dtos = employeService.findAll().stream()
                .map(employeMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consulter un employé par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé trouvé"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeDTO> findById(@Parameter(description = "Identifiant de l'employé") @PathVariable Long id) {
        return ResponseEntity.ok(employeMapper.toDto(employeService.findById(id)));
    }

    @Operation(summary = "Créer un nouvel employé")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employé créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<EmployeDTO> create(@Valid @RequestBody EmployeDTO dto) {
        Employe employe = employeMapper.toEntity(dto);
        if (dto.getAgenceId() != null) {
            Agence agence = agenceRepository.findById(dto.getAgenceId()).orElse(null);
            employe.setAgence(agence);
        }
        Employe saved = employeService.create(employe);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeMapper.toDto(saved));
    }

    @Operation(summary = "Modifier un employé existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeDTO> update(
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long id,
            @Valid @RequestBody EmployeDTO dto) {
        Employe existant = employeService.findById(id);
        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        existant.setRole(dto.getRole());
        if (dto.getAgenceId() != null) {
            existant.setAgence(agenceRepository.findById(dto.getAgenceId()).orElse(null));
        }

        Employe updated = employeService.update(id, existant);
        return ResponseEntity.ok(employeMapper.toDto(updated));
    }

    @Operation(summary = "Supprimer un employé par identifiant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'employé") @PathVariable Long id) {
        employeService.deleteById(id);
    }

    // ─── Atelier 7 : Endpoints d'affectation et désaffectation (Section 1.4) ─────

    @Operation(summary = "Affecter un employé à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé affecté avec succès"),
            @ApiResponse(responseCode = "404", description = "Employé ou agence introuvable")
    })
    @PutMapping("/{employeId}/affecter-agence/{agenceId}")
    public ResponseEntity<EmployeDTO> affecterEmployeAAgence(
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long employeId,
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long agenceId) {
        employeService.affecterEmployeAAgence(employeId, agenceId);
        Employe e = employeService.findById(employeId);
        return ResponseEntity.ok(employeMapper.toDto(e));
    }

    @Operation(summary = "Désaffecter un employé d'une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé désaffecté avec succès"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @PutMapping("/{employeId}/desaffecter-agence")
    public ResponseEntity<EmployeDTO> desaffecterEmployeDeAgence(
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long employeId) {
        employeService.desaffecterEmployeDeAgence(employeId);
        Employe e = employeService.findById(employeId);
        return ResponseEntity.ok(employeMapper.toDto(e));
    }
}
