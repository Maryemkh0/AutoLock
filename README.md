# AutoLoc

Plateforme de gestion de location de véhicules multi-agences — projet réalisé dans le cadre de l'UP ASI (Architecture des Systèmes d'Information) à ESPRIT.

## Objectifs du projet

Développer une application Java/Spring Boot permettant la gestion complète de la location de véhicules à travers plusieurs agences : gestion du parc automobile, des réservations, des clients et des utilisateurs internes.

## Acteurs et cas d'utilisation (identifiés en Séance 1)

### 🧍 Client
- Consulter le catalogue de véhicules disponibles (par agence, dates, catégorie)
- Effectuer une réservation de véhicule
- Annuler ou modifier une réservation
- Consulter l'historique de ses locations
- Gérer son profil (informations personnelles, permis de conduire)

### 🧑‍💼 Agent d'agence
- Enregistrer le départ d'un véhicule (état des lieux, remise des clés)
- Enregistrer le retour d'un véhicule (état des lieux, calcul des éventuels frais)
- Consulter et gérer les réservations de son agence
- Mettre à jour la disponibilité et l'état du parc automobile de son agence
- Créer une réservation pour un client se présentant directement en agence

### 👔 Responsable d'agence
- Superviser l'activité quotidienne de son agence
- Valider ou refuser certaines opérations sensibles (litiges, remises exceptionnelles)
- Consulter les statistiques et le taux d'occupation du parc de son agence
- Gérer les agents rattachés à son agence

### 🛠️ Administrateur
- Gérer l'ensemble des agences (création, modification, suppression)
- Gérer les comptes utilisateurs (clients, agents, responsables) et leurs droits
- Gérer le parc automobile global (ajout, retrait, transfert de véhicules entre agences)
- Consulter les statistiques globales de la plateforme

## Stack technique

- Java 17, Spring Boot, Spring Data JPA, Spring MVC
- MySQL / MariaDB
- Maven
- Lombok
- Tests : JUnit 5, Mockito

## Statut

🚧 Projet en cours de développement — Atelier 0 (mise en place de l'environnement) terminé.
