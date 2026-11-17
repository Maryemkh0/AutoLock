# Atelier 7 – DTO, Bean Validation, Affectation et Refactoring

## A. Objectifs

Cet atelier introduit trois concepts clés pour améliorer la qualité de l'API REST AutoLoc :

1. **DTO (Data Transfer Object)** — Séparer les objets de transfert API des entités JPA
2. **Bean Validation** — Valider les entrées utilisateur avec les annotations `jakarta.validation.*`
3. **Méthodes d'affectation** — Lier des véhicules et des employés à des agences via des endpoints dédiés
4. **Refactoring** — Améliorer les contrôleurs existants (supprimer les vérifications manuelles redondantes)

---

## B. Architecture DTO

### Pourquoi des DTOs ?

| Sans DTO | Avec DTO |
|----------|----------|
| L'API expose directement l'entité JPA | L'API expose un objet dédié au transfert |
| Risque de sérialiser des relations JPA → StackOverflow | Contrôle total sur les champs exposés |
| Pas de validation déclarative | Annotations `@NotBlank`, `@Email`, etc. |
| Couplage fort Controller ↔ BDD | Couche découplée |

### DTOs créés

| DTO | Champs validés |
|-----|----------------|
| `VehiculeDTO` | `@NotBlank` immatriculation, `@DecimalMin(0)` tarif, `@PositiveOrZero` kilométrage |
| `AgenceDTO` | `@NotBlank` nom + ville, `@Pattern` téléphone |
| `ClientDTO` | `@Email` email, `@NotBlank` nom/prénom/permis |
| `ReservationDTO` | `@FutureOrPresent` dateDebut, `@NotNull` idClient + idVehicule |
| `EmployeDTO` | `@NotBlank` nom/prénom, `@NotNull` rôle, `idAgence` optionnel |

---

## C. MapStruct — Mapper automatique

Les mappers dans `web/mapper/` convertissent automatiquement DTO ↔ Entité :

```java
// Le compilateur génère l'implémentation automatiquement
@Mapper(componentModel = "spring")
public interface VehiculeMapper {
    @Mapping(target = "idVehicule", ignore = true)
    @Mapping(target = "agence", ignore = true)
    Vehicule toEntity(VehiculeDTO dto);

    @Mapping(target = "idAgence", source = "agence.idAgence")
    VehiculeDTO toDto(Vehicule vehicule);
}
```

---

## D. Bean Validation — Principe

### Configuration
La dépendance `spring-boot-starter-validation` est ajoutée dans `pom.xml`.

### Utilisation dans les contrôleurs
```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public VehiculeDTO create(@Valid @RequestBody VehiculeDTO dto) {
    // Si un champ invalide → MethodArgumentNotValidException → 400
}
```

### Gestion des erreurs de validation
Le `GlobalExceptionHandler` capte `MethodArgumentNotValidException` et retourne :
```json
{
  "statut": 400,
  "message": "Validation échouée : 2 erreur(s)",
  "erreurs": {
    "immatriculation": "L'immatriculation est obligatoire",
    "tarifJournalier": "Le tarif journalier ne peut pas être négatif"
  },
  "horodatage": "2026-11-17T13:00:00"
}
```

---

## E. Méthodes d'Affectation

Nouvelles méthodes dans `IAgenceService` et implémentées dans `AgenceServiceImpl` :

| Méthode | Endpoint REST | Description |
|---------|---------------|-------------|
| `affecterVehicule(idAgence, idVehicule)` | `PUT /agences/{id}/vehicules/{idV}` | Lie un véhicule à une agence |
| `retirerVehicule(idAgence, idVehicule)` | `DELETE /agences/{id}/vehicules/{idV}` | Détache un véhicule d'une agence |
| `affecterEmploye(idAgence, idEmploye)` | `PUT /agences/{id}/employes/{idE}` | Lie un employé à une agence |

---

## F. Refactoring effectué

| Fichier | Avant | Après |
|---------|-------|-------|
| `VehiculeController` | `@RequestBody Vehicule` | `@Valid @RequestBody VehiculeDTO` + mapper |
| `AgenceController` | `@RequestBody Agence` | `@Valid @RequestBody AgenceDTO` + mapper + endpoints affectation |
| `ClientController` | `@RequestBody Client` | `@Valid @RequestBody ClientDTO` + mapper |
| `EmployeController` | `@RequestBody Employe` | `@Valid @RequestBody EmployeDTO` + mapper |
| `ReservationController` | `@RequestBody Reservation` | `@Valid @RequestBody ReservationDTO` + résolution client/véhicule |
| `GlobalExceptionHandler` | 2 handlers | 3 handlers (+ `MethodArgumentNotValidException`) |

---

## G. Tests (`Atelier7Test.java`)

| # | Test | Vérifie |
|---|------|---------|
| 1 | Mapping `AgenceDTO → Agence` | MapStruct convertit correctement tous les champs |
| 2 | Mapping `VehiculeDTO → Vehicule` | Champs mappés, `idVehicule` et `agence` ignorés |
| 3 | Affectation véhicule → agence | `affecterVehicule()` met à jour `vehicule.agence` |
| 4 | Affectation employé → agence | `affecterEmploye()` met à jour `employe.agence` |
| 5 | Validation tarif négatif | Service lève `IllegalArgumentException` |
| 6 | Mapping + création `ClientDTO` | DTO → entité → service → retour DTO |
