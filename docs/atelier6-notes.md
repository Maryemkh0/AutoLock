# Notes de l'Atelier 6 : Patrons de Conception GoF et Principes SOLID

## Partie 1 : Analyse de AutoLoc v0

### 1.1 Classe problématique : `AutoLocManager.java`
Une première version non refactorisée a été analysée : elle concentre la totalité de la logique dans une seule méthode monolithique `louerVehicule(...)`.

### 1.2 Réponses aux questions d'analyse

#### Question 1 : Combien de responsabilités possède la classe `AutoLocManager` ?
**Réponse :**  
La classe `AutoLocManager` concentre au moins **5 responsabilités distinctes** :
1. **Calcul du prix de location** selon le type de réservation (Standard, Weekend, LongueDurée).
2. **Création du contrat**.
3. **Enregistrement du paiement**.
4. **Gestion des notifications par e-mail**.
5. **Gestion des notifications par SMS**.
6. **Sauvegarde de la persistance du contrat**.

Cette concentration excessive de responsabilités constitue un anti-patron de conception architectural connu sous le nom de **"God Class"** (ou le "Blob").

#### Question 2 : Quel principe SOLID n’est pas respecté ?
**Réponse :**  
Le **Single Responsibility Principle (SRP)** (Principe de Responsabilité Unique).
- *Définition* : Une classe ne doit avoir qu'une seule et unique raison de changer.
- *Constat* : `AutoLocManager` sera modifiée si les tarifs changent, si la façon d'envoyer les e-mails change, si le prestataire de SMS change ou si la persistance du contrat évolue.

#### Question 3 : Que se passe-t-il si AutoLoc ajoute un nouveau type de réservation (ex. Pleinesaison) ?
**Réponse :**  
Pour intégrer un type comme `Pleinesaison`, le développeur est contraint d'ouvrir et de modifier directement le code source existant de la méthode `louerVehicule()` afin d'y ajouter un nouveau bloc conditionnel `else if (typeReservation.equals("Pleinesaison"))`.  
Cette modification directe du code existant comporte un risque élevé d'introduire des régressions ou des effets de bord sur les autres calculs de prix déjà en production.

**Donc, violation de quel principe ?**  
Le **Open/Closed Principle (OCP)** (Principe Ouvert/Fermé).
- *Définition* : Une classe doit être **ouverte à l'extension** (on peut lui ajouter de nouveaux comportements), mais **fermée à la modification** (son code existant ne doit pas être altéré).

---

## Partie 2 : Refactoring avec le Strategy Pattern (Tarification)

### Objectif
Remplacer les cascades de `if / else if` par des objets stratégies interchangeables implémentant une interface commune :
- **`ITarificationStrategy`** : définit le contrat `calculerTarif(Vehicule, Reservation)` et `getCode()`.
- **`TarifStandardStrategy`** : calcul standard au prix de base journalier.
- **`TarifLongueDureeStrategy`** : applique une remise de 20% (`REMISE_LONGUE_DUREE = 0.80`).
- **`TarifWeekendStrategy`** : applique un tarif adapté au weekend avec remise de 10% (`REMISE_WEEKEND = 0.90`).

### Respect des principes SOLID :
- **OCP** : Pour ajouter le tarif `Pleinesaison`, il suffit de créer une nouvelle classe `TarifPleineSaisonStrategy implements ITarificationStrategy` sans modifier aucune classe existante.
- **DIP (Dependency Inversion Principle)** : Les clients dépendent de l'abstraction `ITarificationStrategy`, et non des classes concrètes.

---

## Partie 3 : Factory Pattern

### Objectif
Découpler l'instanciation des objets et centraliser leur résolution :
- **`TarificationStrategyFactory`** : sélectionne dynamiquement la bonne stratégie selon les caractéristiques de la réservation (durée, dates tombant le weekend). Toutes les implémentations Spring sont automatiquement injectées dans une `Map<String, ITarificationStrategy>`.
- **`NotificationFactory`** : méthode de fabrique (`creerObservateur(String canal)`) qui fournit l'observateur approprié (`EMAIL` ou `SMS`) sans exposer les classes concrètes à l'appelant.

---

## Partie 4 : Observer Pattern (Événements de Réservation)

### Objectif
Informer automatiquement les acteurs concernés (client par e-mail, téléphone par SMS, services annexes) lors des changements d'état du cycle de vie d'une réservation, sans couplage fort entre le service de réservation et les canaux de notification.

- **`IReservationObserver`** : Interface observateur commune exposant `onReservationConfirmee(...)` et `onReservationAnnulee(...)`.
- **`ReservationSubject`** : Le sujet observable qui maintient la liste des observateurs et propage les notifications.
- **`EmailNotificationObserver`** : Observateur concret journalisant l'envoi d'e-mail au client.
- **`SmsNotificationObserver`** : Observateur concret journalisant l'envoi de SMS au numéro du client.

---

## Partie 5 : Tests et Validation

La classe [`ReservationServiceTest.java`](file:///c:/xampp/htdocs/AutoLoc-main (1)/AutoLoc-main/src/test/java/tn/esprit/autoloc/ReservationServiceTest.java) valide l'ensemble :
1. **Création d'une réservation** : déclenche automatiquement `notifierConfirmation()` et affiche les logs de confirmation par Email et SMS.
2. **Annulation d'une réservation** : déclenche automatiquement `notifierAnnulation()` et affiche les logs d'annulation par Email et SMS.
3. **Résolution dynamique de tarification** : vérifie que la fabrique choisit la bonne stratégie (Standard vs Longue Durée) et calcule le montant exact.
