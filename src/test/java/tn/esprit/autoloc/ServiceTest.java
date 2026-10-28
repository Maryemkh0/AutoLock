package tn.esprit.autoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.domain.enums.CategorieVehicule;
import tn.esprit.autoloc.domain.enums.RoleEmploye;
import tn.esprit.autoloc.domain.enums.StatutVehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServiceTest {

    @Autowired
    private IAgenceService agenceService;

    @Autowired
    private IClientService clientService;

    @Autowired
    private IVehiculeService vehiculeService;

    @Autowired
    private IContratService contratService;

    @Autowired
    private IEmployeService employeService;

    @Autowired
    private IEquipementService equipementService;

    // ---------------------------------------------------------------
    // Partie 2 : Test guidé de AgenceService
    // ---------------------------------------------------------------
    @Test
    void testAgenceService() {
        System.out.println("=== DEBUT TEST AgenceService ===");

        // 1. Créer une instance d'Agence
        Agence agence = new Agence();
        agence.setNom("AutoLoc Lac 2");
        agence.setVille("Tunis");
        agence.setAdresse("Les Berges du Lac 2");
        agence.setTelephone("71234567");

        // 2. Enregistrer l'agence avec create()
        Agence saved = agenceService.create(agence);
        assertNotNull(saved);
        assertNotNull(saved.getIdAgence());

        // 3. Afficher l'objet retourné
        System.out.println("Agence créée : id=" + saved.getIdAgence() + ", nom=" + saved.getNom());

        // 4. Afficher toutes les agences avec findAll()
        List<Agence> agences = agenceService.findAll();
        System.out.println("Nombre total d'agences trouvées : " + agences.size());
        assertFalse(agences.isEmpty());

        // 5. Récupérer une agence avec findById() et afficher le résultat
        Agence trouvee = agenceService.findById(saved.getIdAgence());
        assertNotNull(trouvee);
        assertEquals("AutoLoc Lac 2", trouvee.getNom());
        System.out.println("Agence retrouvée par id : " + trouvee.getNom());

        // 6. Afficher le nombre d'agences avec count() (size)
        long count = agences.size();
        System.out.println("Nombre d'agences : " + count);
        assertTrue(count >= 1);

        // 7. Supprimer l'agence avec deleteById() puis vérifier qu'elle n'existe plus
        Long id = saved.getIdAgence();
        agenceService.deleteById(id);
        assertThrows(ResourceNotFoundException.class, () -> agenceService.findById(id));
        System.out.println("Agence " + id + " supprimée avec succès et introuvable.");

        System.out.println("=== FIN TEST AgenceService ===\n");
    }

    // ---------------------------------------------------------------
    // Partie 3 : Test guidé de ClientService
    // ---------------------------------------------------------------
    @Test
    void testClientService() {
        System.out.println("=== DEBUT TEST ClientService ===");

        // 1. Créer deux clients
        Client c1 = new Client();
        c1.setNom("Mansour");
        c1.setPrenom("Yassine");
        c1.setEmail("yassine.mansour@example.com");
        c1.setTelephone("98111222");
        c1.setNumPermis("B987654");
        c1.setDateInscription(LocalDate.now());

        Client c2 = new Client();
        c2.setNom("Gharbi");
        c2.setPrenom("Salma");
        c2.setEmail("salma.gharbi@example.com");
        c2.setTelephone("97333444");
        c2.setNumPermis("B123987");
        c2.setDateInscription(LocalDate.now());

        // 2. Enregistrer les deux clients
        Client saved1 = clientService.create(c1);
        Client saved2 = clientService.create(c2);
        assertNotNull(saved1.getIdClient());
        assertNotNull(saved2.getIdClient());
        System.out.println("Client 1 créé : id=" + saved1.getIdClient() + ", nom=" + saved1.getNom());
        System.out.println("Client 2 créé : id=" + saved2.getIdClient() + ", nom=" + saved2.getNom());

        // 3. Afficher la liste avec findAll()
        List<Client> clients = clientService.findAll();
        System.out.println("Liste des clients, total : " + clients.size());
        assertTrue(clients.size() >= 2);

        // 4. Rechercher un client avec findById()
        Client trouve = clientService.findById(saved1.getIdClient());
        assertNotNull(trouve);
        assertEquals("Mansour", trouve.getNom());
        System.out.println("Client retrouvé : " + trouve.getNom() + " " + trouve.getPrenom());

        // 5. Afficher le nombre de clients
        System.out.println("Nombre total de clients en base : " + clients.size());

        // 6. Supprimer le client avec deleteById(), puis vérifier qu'il n'existe plus
        Long idClient = saved2.getIdClient();
        clientService.deleteById(idClient);
        assertThrows(ResourceNotFoundException.class, () -> clientService.findById(idClient));
        System.out.println("Client 2 supprimé et vérifié introuvable.");

        // Nettoyage de c1
        clientService.deleteById(saved1.getIdClient());

        System.out.println("=== FIN TEST ClientService ===\n");
    }

    // ---------------------------------------------------------------
    // Partie 4 : Test guidé de VehiculeService
    // ---------------------------------------------------------------
    @Test
    void testVehiculeService() {
        System.out.println("=== DEBUT TEST VehiculeService ===");

        // 1. Créer un véhicule
        Vehicule v = new Vehicule();
        v.setImmatriculation("250 TUN 1122");
        v.setMarque("Peugeot");
        v.setModele("208");
        v.setCategorie(CategorieVehicule.CITADINE);
        v.setTarifJournalier(new BigDecimal("120.00"));
        v.setKilometrage(15000);
        v.setStatut(StatutVehicule.DISPONIBLE);

        // 2. Enregistrer le véhicule avec create()
        Vehicule saved = vehiculeService.create(v);
        assertNotNull(saved.getIdVehicule());
        System.out.println("Véhicule créé : id=" + saved.getIdVehicule() + ", marque=" + saved.getMarque());

        // 3. Afficher tous les véhicules avec findAll()
        List<Vehicule> vehicules = vehiculeService.findAll();
        System.out.println("Nombre total de véhicules : " + vehicules.size());
        assertFalse(vehicules.isEmpty());

        // 4. Rechercher une voiture avec findById()
        Vehicule trouve = vehiculeService.findById(saved.getIdVehicule());
        assertNotNull(trouve);
        assertEquals("208", trouve.getModele());

        // 5. Afficher le nombre de véhicules
        System.out.println("Nombre de véhicules existants : " + vehicules.size());

        // 6. Modifier une propriété du véhicule puis utiliser update() pour enregistrer la modification
        trouve.setTarifJournalier(new BigDecimal("140.00"));
        Vehicule modifie = vehiculeService.update(trouve.getIdVehicule(), trouve);
        assertEquals(0, new BigDecimal("140.00").compareTo(modifie.getTarifJournalier()));
        System.out.println("Nouveau tarif journalier du véhicule après update : " + modifie.getTarifJournalier());

        // 7. Supprimer le véhicule avec deleteById()
        Long idVehicule = saved.getIdVehicule();
        vehiculeService.deleteById(idVehicule);
        assertThrows(ResourceNotFoundException.class, () -> vehiculeService.findById(idVehicule));
        System.out.println("Véhicule supprimé avec succès.");

        System.out.println("=== FIN TEST VehiculeService ===\n");
    }

    // ---------------------------------------------------------------
    // Partie 5 : Exercice autonome (Contrat, Employe, Equipement)
    // ---------------------------------------------------------------
    @Test
    void testContratService() {
        System.out.println("=== TEST AUTONOME ContratService ===");
        Contrat c = new Contrat();
        c.setDateSignature(LocalDate.now());
        c.setMontantTotal(new BigDecimal("600.00"));
        c.setValide(true);

        Contrat saved = contratService.create(c);
        assertNotNull(saved.getIdContrat());
        System.out.println("Contrat créé : id=" + saved.getIdContrat() + ", montant=" + saved.getMontantTotal());

        Contrat trouve = contratService.findById(saved.getIdContrat());
        assertNotNull(trouve);
        assertTrue(trouve.isValide());

        List<Contrat> contrats = contratService.findAll();
        assertTrue(contrats.size() >= 1);

        contratService.deleteById(saved.getIdContrat());
        assertThrows(ResourceNotFoundException.class, () -> contratService.findById(saved.getIdContrat()));
        System.out.println("Contrat supprimé et vérifié introuvable.");
    }

    @Test
    void testEmployeService() {
        System.out.println("=== TEST AUTONOME EmployeService ===");
        Employe emp = new Employe();
        emp.setNom("Dridi");
        emp.setPrenom("Karim");
        emp.setRole(RoleEmploye.AGENT);

        Employe saved = employeService.create(emp);
        assertNotNull(saved.getIdEmploye());
        System.out.println("Employé créé : id=" + saved.getIdEmploye() + ", nom=" + saved.getNom());

        Employe trouve = employeService.findById(saved.getIdEmploye());
        assertEquals("Dridi", trouve.getNom());

        employeService.deleteById(saved.getIdEmploye());
        assertThrows(ResourceNotFoundException.class, () -> employeService.findById(saved.getIdEmploye()));
        System.out.println("Employé supprimé avec succès.");
    }

    @Test
    void testEquipementService() {
        System.out.println("=== TEST AUTONOME EquipementService ===");
        Equipement eq = new Equipement();
        eq.setLibelle("Siège Bébé");
        eq.setQuantite(5);

        Equipement saved = equipementService.create(eq);
        assertNotNull(saved.getIdEquipement());
        System.out.println("Équipement créé : id=" + saved.getIdEquipement() + ", libelle=" + saved.getLibelle());

        Equipement trouve = equipementService.findById(saved.getIdEquipement());
        assertEquals("Siège Bébé", trouve.getLibelle());

        equipementService.deleteById(saved.getIdEquipement());
        assertThrows(ResourceNotFoundException.class, () -> equipementService.findById(saved.getIdEquipement()));
        System.out.println("Équipement supprimé avec succès.");
    }
}
