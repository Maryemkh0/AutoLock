package tn.esprit.autoloc.web.mapper;

import org.mapstruct.*;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.web.dto.ClientDTO;

/**
 * Mapper MapStruct : ClientDTO <-> Client
 */
@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "idClient", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    Client toEntity(ClientDTO dto);

    ClientDTO toDto(Client client);

    @Mapping(target = "idClient", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    void updateEntityFromDto(ClientDTO dto, @MappingTarget Client client);
}
