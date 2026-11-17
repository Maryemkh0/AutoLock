package tn.esprit.autoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.web.dto.ReservationRequestDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;

/**
 * Mapper MapStruct : Réservation (Atelier 7 - Section 2.5, Page 6)
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "id", source = "idReservation")
    @Mapping(target = "clientId", source = "client.idClient")
    @Mapping(target = "clientNomComplet",
             expression = "java(reservation.getClient() != null ? (reservation.getClient().getPrenom() + \" \" + reservation.getClient().getNom()) : null)")
    @Mapping(target = "vehiculeId", source = "vehicule.idVehicule")
    @Mapping(target = "vehiculeImmatriculation", source = "vehicule.immatriculation")
    @Mapping(target = "tarifEstime", ignore = true)
    ReservationResponseDTO toDto(Reservation reservation);

    @Mapping(target = "idReservation", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "vehicule", ignore = true)
    @Mapping(target = "contrat", ignore = true)
    Reservation toEntity(ReservationRequestDTO dto);
}
