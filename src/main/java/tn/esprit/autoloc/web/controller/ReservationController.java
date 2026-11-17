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
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.ReservationDTO;
import tn.esprit.autoloc.web.mapper.ReservationMapper;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Gestion des réservations")
public class ReservationController {

    private final IReservationService reservationService;
    private final IClientService clientService;
    private final IVehiculeService vehiculeService;
    private final ReservationMapper reservationMapper;

    @Operation(summary = "Lister toutes les réservations")
    @ApiResponse(responseCode = "200", description = "Liste des réservations récupérée avec succès")
    @GetMapping
    public List<ReservationDTO> findAll() {
        return reservationService.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    @Operation(summary = "Consulter une réservation par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @GetMapping("/{id}")
    public ReservationDTO findById(
            @Parameter(description = "Identifiant de la réservation") @PathVariable Long id) {
        return reservationMapper.toDto(reservationService.findById(id));
    }

    @Operation(summary = "Créer une nouvelle réservation",
               description = "Résout automatiquement le client et le véhicule via leurs identifiants")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou date de fin antérieure"),
            @ApiResponse(responseCode = "404", description = "Client ou véhicule introuvable")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDTO create(@Valid @RequestBody ReservationDTO dto) {
        Reservation reservation = reservationMapper.toEntity(dto);

        // Résolution des associations via les IDs
        Client client = clientService.findById(dto.getIdClient());
        Vehicule vehicule = vehiculeService.findById(dto.getIdVehicule());
        reservation.setClient(client);
        reservation.setVehicule(vehicule);

        return reservationMapper.toDto(reservationService.create(reservation));
    }

    @Operation(summary = "Modifier une réservation existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Réservation, client ou véhicule introuvable")
    })
    @PutMapping("/{id}")
    public ReservationDTO update(
            @Parameter(description = "Identifiant de la réservation") @PathVariable Long id,
            @Valid @RequestBody ReservationDTO dto) {

        Reservation existante = reservationService.findById(id);
        reservationMapper.updateEntityFromDto(dto, existante);

        if (dto.getIdClient() != null) {
            existante.setClient(clientService.findById(dto.getIdClient()));
        }
        if (dto.getIdVehicule() != null) {
            existante.setVehicule(vehiculeService.findById(dto.getIdVehicule()));
        }

        return reservationMapper.toDto(reservationService.update(id, existante));
    }

    @Operation(summary = "Supprimer une réservation par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réservation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @Parameter(description = "Identifiant de la réservation") @PathVariable Long id) {
        reservationService.deleteById(id);
    }
}
