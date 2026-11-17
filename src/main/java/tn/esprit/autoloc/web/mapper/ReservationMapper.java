package tn.esprit.autoloc.web.mapper;

import org.mapstruct.*;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.web.dto.ReservationDTO;

/**
 * Mapper MapStruct : ReservationDTO <-> Reservation
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "idReservation", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "vehicule", ignore = true)
    @Mapping(target = "contrat", ignore = true)
    Reservation toEntity(ReservationDTO dto);

    @Mapping(target = "idClient", source = "client.idClient")
    @Mapping(target = "idVehicule", source = "vehicule.idVehicule")
    ReservationDTO toDto(Reservation reservation);

    @Mapping(target = "idReservation", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "vehicule", ignore = true)
    @Mapping(target = "contrat", ignore = true)
    void updateEntityFromDto(ReservationDTO dto, @MappingTarget Reservation reservation);
}
