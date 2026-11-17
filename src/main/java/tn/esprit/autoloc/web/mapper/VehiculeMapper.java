package tn.esprit.autoloc.web.mapper;

import org.mapstruct.*;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

/**
 * Mapper MapStruct : VehiculeDTO <-> Vehicule
 * Convertit automatiquement les champs identiques.
 * idAgence (Long) → agence (Agence) est géré manuellement.
 */
@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    /**
     * Convertit un DTO en entité.
     * idAgence → agence : ignoré ici, géré dans le service.
     */
    @Mapping(target = "idVehicule", ignore = true)
    @Mapping(target = "agence", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "maintenances", ignore = true)
    @Mapping(target = "equipements", ignore = true)
    Vehicule toEntity(VehiculeDTO dto);

    /**
     * Convertit une entité en DTO.
     * agence → idAgence : on extrait l'id de l'agence.
     */
    @Mapping(target = "idAgence", source = "agence.idAgence")
    VehiculeDTO toDto(Vehicule vehicule);

    /**
     * Met à jour une entité existante à partir d'un DTO.
     * Utilisé dans les opérations PUT.
     */
    @Mapping(target = "idVehicule", ignore = true)
    @Mapping(target = "agence", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "maintenances", ignore = true)
    @Mapping(target = "equipements", ignore = true)
    void updateEntityFromDto(VehiculeDTO dto, @MappingTarget Vehicule vehicule);
}
