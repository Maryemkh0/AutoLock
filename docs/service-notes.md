# Notes de l'Atelier 4 : Couche Service, IoC et Injection de Dépendances

## Section A. Modes d'injection comparés

| Critère | Injection par constructeur (recommandée) | Injection par attribut (`@Autowired` sur champ) |
| :--- | :--- | :--- |
| **Champ `final` possible ?** | **Oui** (l'immutabilité est garantie) | **Non** (le champ ne peut pas être `final`) |
| **Dépendance visible dans la signature ?** | **Oui** (explicitement déclarée dans les paramètres du constructeur) | **Non** (dépendance masquée à l'extérieur de la classe) |
| **Utilisable avec `new` hors conteneur sans dépendance `null` ?** | **Oui** (le compilateur impose de fournir les dépendances lors de l'instanciation, ex. tests unitaires) | **Non** (l'instanciation manuelle laisse le champ à `null`, risquant des `NullPointerException`) |
| **Signalé par SonarQube for IDE ?** | **Non** (conforme aux meilleures pratiques Clean Code) | **Oui** (règle **java:S6813** : *"Avoid field injection"*) |

---

## Section B. Services et dépendances injectées

| Service | Dépendances injectées | Mode d'injection | Justification |
| :--- | :--- | :--- | :--- |
| `ContratServiceImpl` | `IContratRepository` | Constructeur (`@RequiredArgsConstructor`) | Gère le cycle de vie du contrat, valide la non-négativité du montant total et orchestre la cascade vers les paiements. |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur (`@RequiredArgsConstructor`) | Assure la validation du nom obligatoire et la mise à jour sélective des coordonnées de l'agence. |
| `ClientServiceImpl` | `IClientRepository` | Constructeur (`@RequiredArgsConstructor`) | Vérifie la présence des identifiants (nom, e-mail) et assure la modification contrôlée sans altérer l'historique. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur (`@RequiredArgsConstructor`) | Valide les contraintes métier numériques (tarif journalier >= 0, kilométrage >= 0). |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur (`@RequiredArgsConstructor`) | Impose un libellé non vide et une quantité positive ou nulle. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur (`@RequiredArgsConstructor`) | Contrôle les attributs obligatoires (nom, rôle de l'employé). |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur (`@RequiredArgsConstructor`) | Garantit la cohérence temporelle : la date de fin ne peut être antérieure à la date de début. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur (`@RequiredArgsConstructor`) | Contrôle la cohérence des dates d'intervention et la validité du coût de maintenance. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur (`@RequiredArgsConstructor`) | Fournit un accès en lecture seule (`findById`, `findAll`) ; toute création ou suppression passe obligatoirement par le parent `Contrat`. |

---

## Section C. Diagnostic des messages d'erreur du conteneur Spring

### Message 1
```text
Description:
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.ContratController
required a bean of type 'tn.esprit.autoloc.service.IContratService' that could not be found.
Action:
Consider defining a bean of type 'tn.esprit.autoloc.service.IContratService' in your configuration.
```
- **Exception sous-jacente** : `NoSuchBeanDefinitionException` (ou encapsulée dans `UnsatisfiedDependencyException`).
- **Bean concerné** : `IContratService`.
- **Deux causes les plus probables côté `ContratServiceImpl`** :
  1. *Oubli de l'annotation stéréotype* : la classe `ContratServiceImpl` n'a pas été annotée avec `@Service` (ou `@Component`).
  2. *Problème de package (scan)* : la classe se trouve dans un package non couvert par le scan de composants activé par `@SpringBootApplication` (situé sous `tn.esprit.autoloc`).
- **Correction** : Ajouter l'annotation `@Service` sur `ContratServiceImpl` et s'assurer que la classe est bien dans `tn.esprit.autoloc.service.impl`.

### Message 2
```text
Description:
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.NotificationController
required a single bean, but 2 were found:
- emailNotificateur
- smsNotificateur
Action:
Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans,
or using @Qualifier to identify the bean that should be consumed.
```
- **Exception sous-jacente** : `NoUniqueBeanDefinitionException`.
- **Bean concerné** : `Notificateur` (implémenté par `emailNotificateur` et `smsNotificateur`).
- **Deux corrections possibles** :
  1. *Correction via `@Primary`* : annoter l'implémentation prioritaire sur sa déclaration de classe :
     ```java
     @Primary
     @Service
     public class EmailNotificateur implements Notificateur { ... }
     ```
  2. *Correction via `@Qualifier`* : préciser le nom du bean cible directement sur le paramètre du constructeur récepteur :
     ```java
     public NotificationController(@Qualifier("emailNotificateur") Notificateur notificateur) {
         this.notificateur = notificateur;
     }
     ```

### Message 3
```text
The dependencies of some of the beans in the application context form a cycle:
clientServiceImpl
| ^
v |
reservationServiceImpl
```
- **Exception sous-jacente** : `BeanCurrentlyInCreationException`.
- **Pourquoi l'injection par constructeur rend-elle ce cycle impossible à contourner ?**  
  L'injection par constructeur exige que toutes les dépendances d'un bean soient complètement créées et disponibles au moment où son constructeur est appelé. Pour instancier `ClientServiceImpl`, Spring doit d'abord instancier `ReservationServiceImpl`. Mais pour instancier `ReservationServiceImpl`, Spring a besoin de `ClientServiceImpl`. Aucun bean ne pouvant être instancié en premier, le conteneur ne peut pas résoudre le graphe.
- **Piste de conception pour supprimer le cycle** :
  Extraire la logique commune ou croisée dans un troisième service médiateur (ex. `ReservationValidationService` ou un service applicatif de coordination), ou faire communiquer les couches via un mécanisme d'événements asynchrones (`ApplicationEventPublisher`), ou encore manipuler uniquement des identifiants (`clientId`) plutôt que d'injecter mutuellement les services complets.

---

## Section D. Anomalies SonarQube for IDE relevées et corrigées

1. **Injection par attribut `@Autowired` (Règle java:S6813)** :
   - *Explication* : L'injection directe sur les champs privés masque les dépendances de la classe, empêche l'utilisation de champs `final` et rend impossible l'instanciation unitaire hors conteneur Spring sans passer par la réflexion.
   - *Correction apportée* : Adoption exclusive de l'injection par constructeur avec des attributs `private final` et l'annotation Lombok `@RequiredArgsConstructor`.
2. **Levée d'exception générique `RuntimeException` (Règle java:S112)** :
   - *Explication* : Lever une exception générique prive la couche appelante d'une sémantique métier précise et empêche une gestion fine des codes HTTP par le gestionnaire d'exceptions global.
   - *Correction apportée* : Remplacement par l'exception métier explicite `ResourceNotFoundException("Ressource", id)`.
3. **Mise à jour directe sans vérification préalable d'existence** :
   - *Explication* : Appeler directement `save()` pour une mise à jour écrase l'enregistrement ou risque d'effacer les collections orphelines, sans avertir de l'absence préalable de la ressource.
   - *Correction apportée* : Chargement préalable systématique via `findById(id)` (qui lève `ResourceNotFoundException`), recopie des attributs modifiables, puis sauvegarde de l'entité managée.

---

## Section E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**  
   Il n'y a aucun `new ContratServiceImpl()` dans le code applicatif. C'est le conteneur Spring IoC (`ApplicationContext`) qui l'exécute au démarrage de l'application via réflexion Java, après avoir scanné l'annotation `@Service` et résolu ses dépendances.
2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**  
   Pour respecter le principe d'Inversion des Dépendances (DIP - SOLID) et favoriser un couplage faible. Le contrôleur dépend uniquement du contrat formel de l'interface, ce qui autorise le remplacement de l'implémentation ou l'injection d'un mock lors des tests unitaires sans jamais modifier le code du contrôleur.
3. **Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.**  
   Un service annoté `@Service` est par défaut un singleton partagé simultanément entre tous les threads des requêtes HTTP. S'il stockait un état mutable (ex: `private Contrat contratEnCours;`), la requête d'un utilisateur B écraserait ce champ pendant que la requête d'un utilisateur A est en train de l'exploiter, provoquant des corruptions de données et des comportements imprévisibles (concurrence critique / race condition).
4. **Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?**  
   Passer directement l'objet reçu écraserait toutes les propriétés non transmises par `null` et, avec l'option `orphanRemoval = true`, viderait accidentellement les relations associées (telles que les paiements du contrat). Charger l'entité permet à Hibernate d'effectuer le *dirty checking* et de ne modifier en base que les champs légitimes.
5. **Quelle différence entre `@Component` et `@Bean` ? Entre `@Primary` et `@Qualifier` ? Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?**  
   - `@Component` s'applique sur une classe pour en faire un bean scanné automatiquement. `@Bean` s'applique sur une méthode au sein d'une classe `@Configuration` pour enregistrer explicitement une instance dans le contexte Spring.
   - `@Primary` définit le bean candidat par défaut sur une classe lorsqu'il existe plusieurs implémentations. `@Qualifier` s'utilise au point d'injection pour sélectionner précisément le bean désiré par son nom.
   - `IPaiementService` n'expose aucune opération de modification car le `Paiement` est dans une relation de composition forte avec `Contrat`. La cohérence financière et comptable impose que les ajouts, modifications ou suppressions de paiements soient strictement gouvernés par le cycle de vie du `Contrat` parent (agrégat DDD).
