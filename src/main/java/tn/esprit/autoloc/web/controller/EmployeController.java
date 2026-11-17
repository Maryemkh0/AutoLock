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
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.web.dto.EmployeDTO;
import tn.esprit.autoloc.web.mapper.EmployeMapper;

import java.util.List;

@RestController
@RequestMapping("/employes")
@RequiredArgsConstructor
@Tag(name = "Employés", description = "Gestion des employés")
public class EmployeController {

    private final IEmployeService employeService;
    private final IAgenceService agenceService;
    private final EmployeMapper employeMapper;

    @Operation(summary = "Lister tous les employés")
    @ApiResponse(responseCode = "200", description = "Liste des employés récupérée avec succès")
    @GetMapping
    public List<EmployeDTO> findAll() {
        return employeService.findAll()
                .stream()
                .map(employeMapper::toDto)
                .toList();
    }

    @Operation(summary = "Consulter un employé par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé trouvé"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @GetMapping("/{id}")
    public EmployeDTO findById(@Parameter(description = "Identifiant de l'employé") @PathVariable Long id) {
        return employeMapper.toDto(employeService.findById(id));
    }

    @Operation(summary = "Créer un nouvel employé")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employé créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeDTO create(@Valid @RequestBody EmployeDTO dto) {
        Employe employe = employeMapper.toEntity(dto);

        // Affectation directe de l'agence si fournie dans le DTO
        if (dto.getIdAgence() != null) {
            Agence agence = agenceService.findById(dto.getIdAgence());
            employe.setAgence(agence);
        }

        return employeMapper.toDto(employeService.create(employe));
    }

    @Operation(summary = "Modifier un employé existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @PutMapping("/{id}")
    public EmployeDTO update(
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long id,
            @Valid @RequestBody EmployeDTO dto) {

        Employe existant = employeService.findById(id);
        employeMapper.updateEntityFromDto(dto, existant);

        if (dto.getIdAgence() != null) {
            Agence agence = agenceService.findById(dto.getIdAgence());
            existant.setAgence(agence);
        }

        return employeMapper.toDto(employeService.update(id, existant));
    }

    @Operation(summary = "Supprimer un employé par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employé supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'employé") @PathVariable Long id) {
        employeService.deleteById(id);
    }
}
