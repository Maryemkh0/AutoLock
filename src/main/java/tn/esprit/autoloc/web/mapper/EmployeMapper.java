package tn.esprit.autoloc.web.mapper;

import org.mapstruct.*;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.web.dto.EmployeDTO;

/**
 * Mapper MapStruct : EmployeDTO <-> Employe
 */
@Mapper(componentModel = "spring")
public interface EmployeMapper {

    @Mapping(target = "idEmploye", ignore = true)
    @Mapping(target = "agence", ignore = true)
    Employe toEntity(EmployeDTO dto);

    @Mapping(target = "idAgence", source = "agence.idAgence")
    EmployeDTO toDto(Employe employe);

    @Mapping(target = "idEmploye", ignore = true)
    @Mapping(target = "agence", ignore = true)
    void updateEntityFromDto(EmployeDTO dto, @MappingTarget Employe employe);
}
