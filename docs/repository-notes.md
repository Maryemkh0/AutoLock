# Notes de l'Atelier 3 : Couche Repository Spring Data JPA

## 1. Choix et justification des interfaces Repository

Dans le cadre du projet **AutoLoc**, les neuf interfaces du package `tn.esprit.autoloc.repository` étendent toutes l'interface `JpaRepository<T, ID>`. 

Bien que Spring Data propose une hiérarchie modulaire (`Repository` -> `CrudRepository` -> `ListCrudRepository` -> `PagingAndSortingRepository` -> `JpaRepository`), le choix de `JpaRepository` est retenu car il cumule le CRUD complet, le renvoi direct des collections sous forme de `List<T>` (contrairement à `Iterable<T>` de `CrudRepository`), les capacités de tri (`Sort`) et de pagination (`Pageable`), ainsi que les méthodes propres à JPA (`flush()`, `saveAndFlush()`, etc.).

| Interface | Étend | Justification |
| :--- | :--- | :--- |
| `IAgenceRepository` | `JpaRepository<Agence, Long>` | Fournit un CRUD complet, le retour direct de collections en `List<Agence>`, le tri et la pagination sur les agences. |
| `IClientRepository` | `JpaRepository<Client, Long>` | CRUD complet, manipulation directe en `List<Client>` facilitant le streaming et l'accès indexé, recherche unitaire avec `Optional`. |
| `IVehiculeRepository` | `JpaRepository<Vehicule, Long>` | CRUD complet, pagination et tri avancés (ex. par marque, statut ou tarif journalier), `saveAndFlush` pour synchronisation immédiate. |
| `IEquipementRepository` | `JpaRepository<Equipement, Long>` | CRUD complet, retour sous forme de `List<Equipement>`, gestion simplifiée des options du parc automobile. |
| `IEmployeRepository` | `JpaRepository<Employe, Long>` | CRUD complet, gestion optimisée de la persistance des employés et lecture en `List<Employe>`. |
| `IReservationRepository` | `JpaRepository<Reservation, Long>` | CRUD complet, gestion du cycle de vie des réservations avec pagination et vérification d'existence (`existsById`). |
| `IContratRepository` | `JpaRepository<Contrat, Long>` | CRUD complet, prise en charge de la persistance managée garantissant la cascade (`CascadeType.ALL`) et `orphanRemoval` vers les paiements. |
| `IPaiementRepository` | `JpaRepository<Paiement, Long>` | Nécessaire pour la lecture des paiements (`findAll()`, `findById()`). Les créations et suppressions transitent obligatoirement par le parent `Contrat`. |
| `IMaintenanceRepository` | `JpaRepository<Maintenance, Long>` | CRUD complet, retour en `List<Maintenance>`, tri par dates d'intervention et persistance synchronisée. |

---

## 2. Anomalies SonarQube for IDE (SonarLint) relevées et corrigées

| Anomalie SonarQube for IDE | Règle / Explication | Correction apportée |
| :--- | :--- | :--- |
| **Utilisation de `@Data` sur les entités JPA** | **Règle java:S6035 (Bonnes pratiques JPA)** : `@Data` génère automatiquement `toString()`, `equals()` et `hashCode()` sur tous les champs. Dans un modèle avec associations bidirectionnelles (`Contrat` ↔ `Paiement`), cela provoque des boucles infinies (`StackOverflowError`), force le chargement intempestif des relations `LAZY`, et perturbe la gestion des identités dans les sets Hibernate avant persistance. | Suppression de `@Data`. Utilisation ciblée de Lombok : `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`. |
| **Risque d'utilisation des suppressions en lot (`deleteAllInBatch`)** | **Intégrité référentielle et contexte de persistance** : `deleteAllInBatch` et `deleteAllByIdInBatch` exécutent directement une requête SQL `DELETE` sans charger les entités dans l'EntityManager. En conséquence, les mécanismes de cascade (`CascadeType.ALL` / `REMOVE`) et `orphanRemoval` sont contournés, risquant de lever des erreurs de contraintes de clés étrangères ou d'engendrer des données orphelines. | Utilisation stricte des méthodes unitaires gérées par le contexte de persistance : `deleteById(id)` et `delete(entity)` qui déclenchent les cascades Hibernate prévues. |
| **Classes de test vides et imports inutilisés** | **Règles java:S1128 et java:S2187** : Fichiers résiduels sans cas de test (`testAgenceRepository.java`, `testClientRepository.java`) et imports superflus dans les fichiers de code, polluant la lisibilité et l'analyse statique. | Suppression des fichiers résiduels et nettoyage des imports inutilisés dans l'intégralité du projet. |

---

## 3. Observations des logs SQL et comportement CRUD

- **Création (`save` avec ID null)** : Hibernate déclenche un `INSERT` en base de données. L'identifiant auto-incrémenté (`GenerationType.IDENTITY`) est généré par MySQL et immédiatement affecté à l'objet retourné.
- **Mise à jour (`save` avec ID renseigné)** : Hibernate effectue d'abord un `SELECT` pour charger l'entité existante dans le contexte de persistance (dirty checking), puis un `UPDATE` uniquement sur les colonnes modifiées.
- **Recherche (`findById`)** : Déclenche un `SELECT` filtré par clé primaire (`WHERE id = ?`). La méthode renvoie un `Optional<T>` permettant d'éviter les `NullPointerException`.
- **Suppression (`deleteById`)** : Spring Data vérifie l'entité ou effectue le `DELETE WHERE id = ?`. Les règles de cascade définies (ex. Contrat -> Paiement) suppriment automatiquement les entités dépendantes.
