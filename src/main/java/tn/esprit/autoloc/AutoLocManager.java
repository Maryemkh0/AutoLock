package tn.esprit.autoloc;

/**
 * Atelier 6 - Partie 1 : Classe problématique (AutoLoc v0)
 *
 * Analyse du problème :
 * - Question 1 : Combien de responsabilités possède la classe AutoLocManager ?
 *   Réponse : Elle concentre au moins 5 responsabilités distinctes (calcul du prix,
 *   création de contrat, envoi d'emails, envoi de SMS, enregistrement de paiement,
 *   sauvegarde de contrat). C'est un anti-patron de conception "God Class" (Blob).
 *
 * - Question 2 : Quel principe SOLID n'est pas respecté ?
 *   Réponse : Le Single Responsibility Principle (SRP) - une classe ne doit avoir
 *   qu'une seule raison de changer.
 *
 * - Question 3 : Que se passe-t-il si AutoLoc ajoute un nouveau type de réservation (ex. Pleinesaison) ?
 *   Réponse : Il faut modifier le code existant de louerVehicule() en ajoutant un "else if",
 *   avec un risque élevé de régression sur le code existant.
 *   Violation du principe : Open/Closed Principle (OCP) - ouvert à l'extension, fermé à la modification.
 */
public class AutoLocManager {

    public void louerVehicule(String typeReservation, int nombreJours, String clientEmail) {
        double prix = 0;
        if ("LongDuree".equalsIgnoreCase(typeReservation)) {
            prix = nombreJours * 120;
        } else if ("Weekend".equalsIgnoreCase(typeReservation)) {
            prix = nombreJours * 180;
        } else if ("Standard".equalsIgnoreCase(typeReservation)) {
            prix = nombreJours * 150;
        }

        System.out.println("Contrat créé");
        System.out.println("Prix total : " + prix);

        envoyerEmail(clientEmail);
        enregistrerPaiement();
        sauvegarderContrat();
    }

    private void envoyerEmail(String email) {
        System.out.println("Email envoyé à " + email);
    }

    private void envoyerSMS(String sms) {
        System.out.println("SMS envoyé à " + sms);
    }

    private void enregistrerPaiement() {
        System.out.println("Paiement enregistré");
    }

    private void sauvegarderContrat() {
        System.out.println("Contrat sauvegardé");
    }
}
