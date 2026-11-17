package tn.esprit.autoloc.web.mapper;

import org.mapstruct.*;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.web.dto.AgenceDTO;

/**
 * Mapper MapStruct : AgenceDTO <-> Agence
 */
@Mapper(componentModel = "spring")
public interface AgenceMapper {

    @Mapping(target = "idAgence", ignore = true)
    @Mapping(target = "vehicules", ignore = true)
    @Mapping(target = "employes", ignore = true)
    Agence toEntity(AgenceDTO dto);

    AgenceDTO toDto(Agence agence);

    @Mapping(target = "idAgence", ignore = true)
    @Mapping(target = "vehicules", ignore = true)
    @Mapping(target = "employes", ignore = true)
    void updateEntityFromDto(AgenceDTO dto, @MappingTarget Agence agence);
}
