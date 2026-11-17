# Atelier 7 — Spring Data JPA : Affectation, DTO, Refactoring, SonarLint & Validation

Ce document récapitule l'implémentation complète de l'**Atelier 7** pour l'application **AutoLoc**.

---

## Sommaire
1. [Partie 1 — Affectation et désaffectation avec Spring Data JPA](#partie-1--affectation-et-désaffectation)
2. [Partie 2 — Création et utilisation des DTO avec MapStruct](#partie-2--création-et-utilisation-des-dto)
3. [Partie 3 — Adaptation des Services et Contrôleurs](#partie-3--adaptation-des-services-et-contrôleurs)
4. [Partie 4 — Qualité du code et analyse SonarLint](#partie-4--qualité-du-code-et-sonarlint)
5. [Partie 5 — Validation des données (Bean Validation & Spring Validator)](#partie-5--validation-des-données)
6. [Guide de Test (JUnit, Swagger UI, Postman)](#guide-de-test)

---

## Partie 1 — Affectation et désaffectation

### 1.1 Modèle relationnel
- **Véhicule — Agence** : `@ManyToOne` côté propriétaire (`Vehicule.agence`), `@OneToMany(mappedBy="agence")` côté `Agence`.
- **Employé — Agence** : `@ManyToOne` côté propriétaire (`Employe.agence`), `@OneToMany(mappedBy="agence")` côté `Agence`.

### 1.2 & 1.3 Opérations Véhicule (dans `IVehiculeService` et `VehiculeServiceImpl`)
- `affecterVehiculeAAgence(Long vehiculeId, Long agenceId)` :
  - Vérifie l'existence du véhicule et de l'agence (sinon `ResourceNotFoundException`).
  - Met à jour l'association sur le véhicule et sauvegarde.
- `desaffecterVehiculeDeAgence(Long vehiculeId)` :
  - Met à `null` l'agence sur le véhicule et sauvegarde.
- **Endpoints REST PUT** :
  - `PUT /api/vehicules/{vehiculeId}/affecter-agence/{agenceId}`
  - `PUT /api/vehicules/{vehiculeId}/desaffecter-agence`

### 1.4 Opérations Employé (dans `IEmployeService` et `EmployeServiceImpl`)
- `affecterEmployeAAgence(Long employeId, Long agenceId)`
- `desaffecterEmployeDeAgence(Long employeId)`
- **Endpoints REST PUT** :
  - `PUT /api/employes/{employeId}/affecter-agence/{agenceId}`
  - `PUT /api/employes/{employeId}/desaffecter-agence`

---

## Partie 2 — Création et utilisation des DTO

Les DTO découplent le schéma de la base de données de l'API REST et évitent les fuites d'entités JPA.

| DTO | Attributs | Rôle |
|---|---|---|
| `AgenceDTO` | `id, nom, ville, adresse, telephone` | Données d'une agence |
| `ClientDTO` | `id, nom, prenom, email, telephone, numPermis, dateInscription` | Données client |
| `ContratDTO` | `id, dateSignature, montantTotal, valide, reservationId` | Contrat de location |
| `PaiementDTO` | `id, montant, datePaiement, modePaiement, contratId` | Paiement associé à un contrat |
| `ReservationRequestDTO` | `clientId, vehiculeId, dateDebut, dateFin` | Données reçues pour créer/modifier |
| `ReservationResponseDTO`| `id, dateDebut, dateFin, statut, clientId, clientNomComplet, vehiculeId, vehiculeImmatriculation, tarifEstime` | Données enrichies renvoyées |
| `VehiculeDTO` | `id, immatriculation, marque, modele, categorie, tarifJournalier, statut, agenceId` | Données véhicule |
| `EmployeDTO` | `id, nom, prenom, role, agenceId` | Données employé |

### Mappers MapStruct (`tn.esprit.autoloc.web.mapper`)
MapStruct génère automatiquement les classes d'implémentation lors de la compilation (`componentModel = "spring"`).
Pour `ReservationMapper`, les champs relationnels sont enrichis :
- `clientId` ← `client.idClient`
- `clientNomComplet` ← `client.prenom + " " + client.nom`
- `vehiculeId` ← `vehicule.idVehicule`
- `vehiculeImmatriculation` ← `vehicule.immatriculation`

---

## Partie 3 — Adaptation des Services et Contrôleurs

- Aucun contrôleur ne retourne directement une entité JPA.
- Tous les contrôleurs renvoient des `ResponseEntity<DTO>` ou `ResponseEntity<List<DTO>>`.
- Les requêtes POST et PUT sont validées via `@Valid`.

---

## Partie 4 — Qualité du code et SonarLint

Lors de l'analyse SonarLint / SonarQube for IDE sur le projet, plusieurs règles clés ont été prises en compte et corrigées :

1. **Suppression des imports inutilisés** (Règle `java:S1128`) :
   - Nettoyage des imports inutiles dans les classes de services et contrôleurs.
2. **Encapsulation et injection par constructeur** (Règle `java:S6813`) :
   - Utilisation de `@RequiredArgsConstructor` avec `private final` pour toutes les dépendances Spring (pas de `@Autowired` sur champs).
3. **Gestion précise des exceptions** (Règle `java:S112`) :
   - Remplacement des exceptions génériques par `ResourceNotFoundException` et `IllegalArgumentException`.
4. **Immutabilité et sécurité des DTOs** (Règle `java:S1948`) :
   - Pas de logique métier dans les DTOs ; seules les annotations Lombok et Bean Validation sont présentes.
5. **Gestion transactionnelle** (Règle `java:S2229`) :
   - Ajout explicite de `@Transactional` sur les méthodes d'affectation et de désaffectation.

---

## Partie 5 — Validation des données

### 5.1 & 5.2 Contraintes Bean Validation
- **AgenceDTO** : `@NotBlank` sur nom et ville.
- **ClientDTO** : `@NotBlank` sur nom et prénom ; `@NotBlank` + `@Email` sur email.
- **ContratDTO** : `@PositiveOrZero` sur montantTotal.
- **PaiementDTO** : `@NotNull` + `@Positive` sur montant ; `@NotNull` sur modePaiement et contratId.
- **ReservationRequestDTO** : `@NotNull` sur clientId, vehiculeId, dateDebut, dateFin.
- **VehiculeDTO** : `@NotBlank` sur immatriculation, marque, modèle ; `@NotNull` sur catégorie ; `@Positive` sur tarifJournalier.

### 5.3 GlobalExceptionHandler
Gère `MethodArgumentNotValidException` et renvoie une réponse HTTP 400 structurée :
```json
{
  "immatriculation": "L'immatriculation est obligatoire",
  "tarifJournalier": "Le tarif journalier doit être strictement positif"
}
```

### 5.4 & 5.5 Validateur personnalisé `ReservationRequestValidator`
Implémente `org.springframework.validation.Validator` :
- Vérifie que `dateDebut >= LocalDate.now()` (erreur `reservation.dateDebut.passee`).
- Vérifie que `dateFin > dateDebut` (erreur `reservation.dates.invalides`).
- Injecté et appelé explicitement dans `ReservationController.creer()` avant la persistance.

---

## Guide de Test

### 1. Exécution des tests automatisés (JUnit 5)
Dans le terminal :
```bash
.\mvnw.cmd test -Dtest=Atelier7Test
```
Ou l'ensemble des tests du projet (28 tests) :
```bash
.\mvnw.cmd clean test
```

### 2. Test via Swagger UI
1. Démarrer le serveur :
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
2. Ouvrir le navigateur : [http://localhost:8090/api/swagger-ui.html](http://localhost:8090/api/swagger-ui.html)
3. Tester les endpoints :
   - `PUT /api/vehicules/{id}/affecter-agence/{agenceId}`
   - `PUT /api/vehicules/{id}/desaffecter-agence`
   - `POST /api/reservations` avec dateDebut passée ou dateFin < dateDebut pour voir le code 400.

### 3. Test via Postman
- **Créer un véhicule sans immatriculation** (`POST /api/vehicules`) :
  ```json
  {
    "immatriculation": "",
    "marque": "Renault",
    "modele": "Clio",
    "categorie": "CITADINE",
    "tarifJournalier": 50.0
  }
  ```
  ➜ Réponse attendue : **400 Bad Request** :
  ```json
  {
    "immatriculation": "L'immatriculation est obligatoire"
  }
  ```

- **Affecter un véhicule à une agence** (`PUT /api/vehicules/1/affecter-agence/1`) :
  ➜ Réponse attendue : **200 OK** avec le DTO du véhicule contenant `agenceId: 1`.

- **Créer une réservation avec date de fin incohérente** (`POST /api/reservations`) :
  ```json
  {
    "clientId": 1,
    "vehiculeId": 1,
    "dateDebut": "2026-12-10",
    "dateFin": "2026-12-05"
  }
  ```
  ➜ Réponse attendue : **400 Bad Request** :
  ```json
  [
    "dateFin : La date de fin doit être postérieure à la date de début"
  ]
  ```
