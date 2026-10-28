# Notes de l'Atelier 5 : API REST Spring MVC, Postman et Documentation OpenAPI / Swagger

## Section A. Tableau récapitulatif des Endpoints REST

Le préfixe commun `/api` est configuré au niveau de `server.servlet.context-path=/api` dans `application.properties`.

| Contrôleur | Verbe & Route | Méthode Service appelée | Code Succès | Codes d'Erreur | Description |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `ContratController` | `GET /api/contrats` | `contratService.findAll()` | `200 OK` | — | Lister tous les contrats |
| `ContratController` | `GET /api/contrats/{id}` | `contratService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un contrat |
| `ContratController` | `POST /api/contrats` | `contratService.create(contrat)` | `201 Created` | `400 Bad Request` | Créer un contrat (sans id) |
| `ContratController` | `PUT /api/contrats/{id}` | `contratService.update(id, contrat)` | `200 OK` | `400, 404` | Modifier un contrat existant |
| `ContratController` | `DELETE /api/contrats/{id}` | `contratService.deleteById(id)` | `204 No Content`| `404 Not Found` | Supprimer un contrat |
| `PaiementController` | `GET /api/paiements` | `paiementService.findAll()` | `200 OK` | — | Lister tous les paiements |
| `PaiementController` | `GET /api/paiements/{id}` | `paiementService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un paiement |
| `PaiementController` | `POST /api/paiements` | *(Non implémenté)* | — | `405 Method Not Allowed` | Rejet création directe de paiement |
| `AgenceController` | `GET /api/agences` | `agenceService.findAll()` | `200 OK` | — | Lister les agences |
| `AgenceController` | `GET /api/agences/{id}` | `agenceService.findById(id)` | `200 OK` | `404 Not Found` | Consulter une agence |
| `AgenceController` | `POST /api/agences` | `agenceService.create(agence)` | `201 Created` | `400 Bad Request` | Créer une agence (nom obligatoire) |
| `AgenceController` | `PUT /api/agences/{id}` | `agenceService.update(id, agence)` | `200 OK` | `400, 404` | Modifier une agence |
| `AgenceController` | `DELETE /api/agences/{id}` | `agenceService.deleteById(id)` | `204 No Content`| `404 Not Found` | Supprimer une agence |
| `ClientController` | `GET /api/clients` | `clientService.findAll()` | `200 OK` | — | Lister les clients |
| `ClientController` | `GET /api/clients/{id}` | `clientService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un client |
| `ClientController` | `POST /api/clients` | `clientService.create(client)` | `201 Created` | `400 Bad Request` | Créer un client (nom et e-mail requis) |
| `ClientController` | `PUT /api/clients/{id}` | `clientService.update(id, client)` | `200 OK` | `400, 404` | Modifier un client |
| `ClientController` | `DELETE /api/clients/{id}` | `clientService.deleteById(id)` | `204 No Content`| `404 Not Found` | Supprimer un client |
| `VehiculeController` | `GET /api/vehicules` | `vehiculeService.findAll()` | `200 OK` | — | Lister les véhicules |
| `VehiculeController` | `GET /api/vehicules/{id}` | `vehiculeService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un véhicule |
| `VehiculeController` | `POST /api/vehicules` | `vehiculeService.create(vehicule)` | `201 Created` | `400 Bad Request` | Créer un véhicule (tarif >= 0) |
| `VehiculeController` | `PUT /api/vehicules/{id}` | `vehiculeService.update(id, vehicule)` | `200 OK` | `400, 404` | Modifier un véhicule |
| `VehiculeController` | `DELETE /api/vehicules/{id}` | `vehiculeService.deleteById(id)` | `204 No Content`| `404 Not Found` | Supprimer un véhicule |
| `EmployeController` | `GET /api/employes` | `employeService.findAll()` | `200 OK` | — | Lister les employés |
| `EmployeController` | `GET /api/employes/{id}` | `employeService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un employé |
| `EmployeController` | `POST /api/employes` | `employeService.create(employe)` | `201 Created` | `400 Bad Request` | Créer un employé (nom & rôle requis) |
| `EmployeController` | `PUT /api/employes/{id}` | `employeService.update(id, employe)` | `200 OK` | `400, 404` | Modifier un employé |
| `EmployeController` | `DELETE /api/employes/{id}` | `employeService.deleteById(id)` | `204 No Content`| `404 Not Found` | Supprimer un employé |
| `EquipementController`| `GET /api/equipements` | `equipementService.findAll()` | `200 OK` | — | Lister les équipements |
| `EquipementController`| `GET /api/equipements/{id}` | `equipementService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un équipement |
| `EquipementController`| `POST /api/equipements` | `equipementService.create(eq)` | `201 Created` | `400 Bad Request` | Créer un équipement (libellé requis) |
| `EquipementController`| `PUT /api/equipements/{id}` | `equipementService.update(id, eq)` | `200 OK` | `400, 404` | Modifier un équipement |
| `EquipementController`| `DELETE /api/equipements/{id}`| `equipementService.deleteById(id)` | `204 No Content`| `404 Not Found` | Supprimer un équipement |
| `ReservationController`| `GET /api/reservations` | `reservationService.findAll()` | `200 OK` | — | Lister les réservations |
| `ReservationController`| `GET /api/reservations/{id}` | `reservationService.findById(id)`| `200 OK` | `404 Not Found` | Consulter une réservation |
| `ReservationController`| `POST /api/reservations` | `reservationService.create(res)` | `201 Created` | `400 Bad Request` | Créer réservation (dateFin >= dateDebut) |
| `ReservationController`| `PUT /api/reservations/{id}` | `reservationService.update(id, res)`| `200 OK` | `400, 404` | Modifier une réservation |
| `ReservationController`| `DELETE /api/reservations/{id}`| `reservationService.deleteById(id)`| `204 No Content`| `404 Not Found` | Supprimer une réservation |
| `MaintenanceController`| `GET /api/maintenances` | `maintenanceService.findAll()` | `200 OK` | — | Lister les maintenances |
| `MaintenanceController`| `GET /api/maintenances/{id}`| `maintenanceService.findById(id)`| `200 OK` | `404 Not Found` | Consulter une maintenance |
| `MaintenanceController`| `POST /api/maintenances` | `maintenanceService.create(m)` | `201 Created` | `400 Bad Request` | Créer maintenance (coût >= 0) |
| `MaintenanceController`| `PUT /api/maintenances/{id}`| `maintenanceService.update(id, m)`| `200 OK` | `400, 404` | Modifier une maintenance |
| `MaintenanceController`| `DELETE /api/maintenances/{id}`| `maintenanceService.deleteById(id)`| `204 No Content`| `404 Not Found` | Supprimer une maintenance |

---

## Section B. Annotations Jackson et gestion des associations bidirectionnelles

| Association | Annotation retenue | Justification |
| :--- | :--- | :--- |
| `Contrat` ↔ `Paiement` | `@JsonManagedReference("contrat-paiements")` sur `Contrat.paiements`<br>`@JsonBackReference("contrat-paiements")` sur `Paiement.contrat` | Vraie relation de composition : les paiements font partie intégrante du contrat. La liste est sérialisée dans le JSON du contrat, tandis que le retour vers le contrat est omis pour éviter la boucle infinie (`StackOverflowError`). |
| `Agence` ↔ `Vehicule` | `@JsonIgnore` sur `Agence.vehicules` | L'enfant `Vehicule` est créé et géré de façon indépendante en désignant son agence par son identifiant `{ "agence": { "idAgence": 1 } }`. Ignorer la collection dans l'agence évite le cycle et le chargement volumineux de tous les véhicules. |
| `Agence` ↔ `Employe` | `@JsonIgnore` sur `Agence.employes` | Évite le chargement paresseux non initialisé (`ByteBuddyInterceptor`) et coupe la boucle de sérialisation. |
| `Client` ↔ `Reservation`| `@JsonIgnore` sur `Client.reservations` | Les réservations portent la clé étrangère vers le client. Couper la liste côté client évite la boucle. |
| `Vehicule` ↔ `Reservation`| `@JsonIgnore` sur `Vehicule.reservations`| Évite les boucles cycliques lors de la consultation des détails d'un véhicule. |
| `Vehicule` ↔ `Maintenance`| `@JsonIgnore` sur `Vehicule.maintenances`| Les opérations de maintenance désignent leur véhicule ; la liste inverse est ignorée lors de la sérialisation JSON. |
| `Reservation` ↔ `Contrat`| `@JsonIgnore` sur `Reservation.contrat` | Relation 1-à-1 : permet de consulter le contrat avec sa réservation sans boucler en sens inverse. |

### Différence entre `@JsonBackReference` et `@JsonIgnore` :
- **`@JsonBackReference`** fonctionne impérativement de concert avec `@JsonManagedReference`. Elle indique à Jackson que la propriété est le côté esclave d'une association bidirectionnelle : elle est ignorée lors de la sérialisation en JSON, mais lors de la désérialisation du parent, Jackson rétablit automatiquement la référence de l'enfant vers le parent.
- **`@JsonIgnore`** exclut inconditionnellement le champ annoté de toute sérialisation ET désérialisation JSON. Elle est particulièrement adaptée aux collections inverses des entités indépendantes créées individuellement.

---

## Section C. Postman et Swagger UI : Complémentarité

| Outil | Ce qu'il apporte | Exemple concret rencontré dans l'atelier |
| :--- | :--- | :--- |
| **Postman** | Client HTTP dynamique permettant d'automatiser des suites de tests, de chaîner des requêtes grâce aux variables d'environnement (`contratId`), et de valider les assertions sur les codes HTTP de retour et les corps de réponses. | Lors de la création d'un contrat par `POST /api/contrats`, le script After-Response extrait l'identifiant généré (`pm.environment.set("contratId", pm.response.json().idContrat)`), réutilisé immédiatement pour le test de modification (`PUT /api/contrats/{{contratId}}`) et de suppression en cascade. |
| **Swagger UI (SpringDoc OpenAPI)** | Génération dynamique et interactive du contrat d'interface (OpenAPI 3.0), synchronisée avec le code source (via `@Tag`, `@Operation`, `@ApiResponse`), fournissant une documentation vivante testable dans le navigateur. | Permet à toute équipe cliente (ex. frontend) de visualiser immédiatement le schéma des entités (`Contrat`, `Vehicule`), les routes disponibles sous `/api/swagger-ui.html`, et d'exécuter des requêtes d'essai (*Try it out*) sans configuration externe. |

---

## Section D. Anomalies SonarQube for IDE relevées et corrigées

1. **Injection par attribut dans les contrôleurs (Règle java:S6813)** :
   - *Explication* : L'annotation `@Autowired` sur les champs privés de services empêche l'immutabilité et complique les tests unitaires isolés.
   - *Correction apportée* : Déclaration des services en `private final` et génération du constructeur requis via Lombok `@RequiredArgsConstructor`.
2. **Couplage contrôleur/entités JPA direct (Exposition directe du modèle)** :
   - *Explication* : L'exposition directe des entités JPA dans les signatures des contrôleurs REST expose les détails de persistance internes et peut déclencher des boucles Jackson ou des erreurs de proxys LAZY (`ByteBuddyInterceptor`).
   - *Correction apportée* : Ajout des annotations Jackson de découplage (`@JsonManagedReference`, `@JsonBackReference`, `@JsonIgnore`) et configuration de `spring.jackson.serialization.fail-on-empty-beans=false` en attendant la mise en place des DTO au Chapitre 10.

---

## Section E. Questions de compréhension

1. **Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?**  
   C'est la **`DispatcherServlet`** (le Front Controller de Spring MVC) qui intercepte l'ensemble des requêtes HTTP entrantes. Elle consulte ensuite le composant **`HandlerMapping`** (notamment `RequestMappingHandlerMapping`), qui compare la méthode HTTP (GET, POST...) et l'URI demandée avec les correspondances déclarées via les annotations `@RequestMapping`, `@GetMapping`, etc., pour identifier la méthode exacte du `@RestController` à exécuter.

2. **Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?**  
   - Il ne dépend pas de `ContratRepository` car un contrôleur ne doit jamais accéder directement à la base de données : il délègue le traitement à la couche métier (`Service`), qui porte les validations, règles d'intégrité et gestion des transactions.
   - Il ne dépend pas de `ContratServiceImpl` afin de respecter le principe d'Inversion des Dépendances (DIP - SOLID) : le contrôleur s'appuie sur une interface abstraite (`IContratService`), assurant un couplage faible et permettant la substitution de l'implémentation ou l'utilisation de mocks pour les tests.

3. **Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?**  
   - `POST` renvoie **`201 Created`** conformément à la spécification HTTP REST lorsqu'une nouvelle ressource est créée avec succès sur le serveur (avec son nouvel URI/identifiant).
   - `DELETE` renvoie **`204 No Content`** lorsque l'opération a réussi et qu'aucun contenu n'a besoin d'être renvoyé dans le corps de la réponse.
   - Une ressource absente doit renvoyer **`404 Not Found`** car il s'agit d'une erreur fonctionnelle côté client (l'identifiant demandé n'existe pas en base), et non **`500 Internal Server Error`**, qui est réservé aux pannes imprévues ou aux exceptions non gérées du serveur.

4. **Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?**  
   `PaiementController` n'expose que des requêtes de lecture car dans la logique métier d'AutoLoc, un paiement ne vit jamais seul : il appartient obligatoirement à un contrat de location (composition et cascade persistante). Un appel `POST /api/paiements` ne correspondant à aucune méthode avec `@PostMapping` dans le contrôleur, Spring MVC renvoie automatiquement un statut HTTP **`405 Method Not Allowed`**.

5. **Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?**  
   - *Risques de l'exposition directe* : fuite d'informations sensibles ou internes de la base, boucles infinies de sérialisation Jackson (`StackOverflowError`), échecs sur les proxys Hibernate paresseux (`LazyInitializationException` ou erreur de sérialiseur ByteBuddy), et possibilité pour un client malveillant de modifier des champs sensibles (sur-assignation ou *mass assignment*).
   - *Différence `Contrat.paiements` vs `Agence.vehicules`* :
     - `Contrat.paiements` forme une **composition forte** : lors de la consultation ou création d'un contrat, on souhaite inclure et afficher la liste des paiements associés. `@JsonManagedReference` sérialise cette liste tout en ordonnant à `@JsonBackReference` sur `Paiement.contrat` de couper la récursion inverse.
     - `Agence.vehicules` représente une relation où `Vehicule` est une ressource racine indépendante, manipulée par son propre contrôleur (`/api/vehicules`). On ne souhaite pas charger ou transférer toute la flotte automobile à chaque consultation d'une agence, d'où l'exclusion nette par `@JsonIgnore`.
