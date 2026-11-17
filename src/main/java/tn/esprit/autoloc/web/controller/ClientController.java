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
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.web.dto.ClientDTO;
import tn.esprit.autoloc.web.mapper.ClientMapper;

import java.util.List;

/**
 * Contrôleur REST Clients (Atelier 7 - Section 3.3, 5.2)
 * Utilise ClientDTO avec validation sur nom, prénom, email.
 */
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Gestion des clients AutoLoc")
public class ClientController {

    private final IClientService clientService;
    private final ClientMapper clientMapper;

    @Operation(summary = "Lister tous les clients")
    @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<ClientDTO>> findAll() {
        List<ClientDTO> dtos = clientService.findAll().stream()
                .map(clientMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consulter un client par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> findById(@Parameter(description = "Identifiant du client") @PathVariable Long id) {
        return ResponseEntity.ok(clientMapper.toDto(clientService.findById(id)));
    }

    @Operation(summary = "Créer un nouveau client")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<ClientDTO> create(@Valid @RequestBody ClientDTO dto) {
        Client client = clientMapper.toEntity(dto);
        Client saved = clientService.create(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientMapper.toDto(saved));
    }

    @Operation(summary = "Modifier un client existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> update(
            @Parameter(description = "Identifiant du client") @PathVariable Long id,
            @Valid @RequestBody ClientDTO dto) {
        Client existant = clientService.findById(id);
        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        existant.setEmail(dto.getEmail());
        existant.setTelephone(dto.getTelephone());
        existant.setNumPermis(dto.getNumPermis());
        if (dto.getDateInscription() != null) {
            existant.setDateInscription(dto.getDateInscription());
        }
        Client updated = clientService.update(id, existant);
        return ResponseEntity.ok(clientMapper.toDto(updated));
    }

    @Operation(summary = "Supprimer un client par identifiant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du client") @PathVariable Long id) {
        clientService.deleteById(id);
    }
}
