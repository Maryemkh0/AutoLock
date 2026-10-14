package com.example.autoloc;

import com.example.autoloc.domain.Agence;
import com.example.autoloc.domain.Client;
import com.example.autoloc.domain.Contrat;
import com.example.autoloc.domain.Equipement;
import com.example.autoloc.domain.Maintenance;
import com.example.autoloc.domain.Vehicule;
import com.example.autoloc.repository.IAgenceRepository;
import com.example.autoloc.repository.IClientRepository;
import com.example.autoloc.repository.IContratRepository;
import com.example.autoloc.repository.IEquipementRepository;
import com.example.autoloc.repository.IMaintenanceRepository;
import com.example.autoloc.repository.IVehiculeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RepositoryTests {

    @Autowired
    private IAgenceRepository agenceRepository;

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private IVehiculeRepository vehiculeRepository;

    @Autowired
    private IEquipementRepository equipementRepository;

    @Autowired
    private IMaintenanceRepository maintenanceRepository;

    @Autowired
    private IContratRepository contratRepository;

    // ---------------------------------------------------------------
    // Partie 2 : AgenceRepository
    // ---------------------------------------------------------------
    @Test
    void testAgenceRepository() {
        Agence agence = new Agence();
        agence.setNom("AutoLoc Tunis");
        agence.setVille("Tunis");
        agence.setAdresse("10 Avenue Habib Bourguiba");
        agence.setTelephone("71000000");

        Agence saved = agenceRepository.save(agence);
        System.out.println("Agence enregistrée : id=" + saved.getIdAgence()
                + ", nom=" + saved.getNom() + ", ville=" + saved.getVille());
        assertNotNull(saved.getIdAgence());

        List<Agence> agences = agenceRepository.findAll();
        System.out.println("Nombre d'agences dans la liste : " + agences.size());
        assertFalse(agences.isEmpty());

        Optional<Agence> trouvee = agenceRepository.findById(saved.getIdAgence());
        assertTrue(trouvee.isPresent());
        assertEquals("AutoLoc Tunis", trouvee.get().getNom());
        System.out.println("Agence retrouvée : " + trouvee.get().getNom());

        assertTrue(agenceRepository.existsById(saved.getIdAgence()));

        long total = agenceRepository.count();
        System.out.println("Total d'agences : " + total);
        assertTrue(total >= 1);

        agenceRepository.deleteById(saved.getIdAgence());
        assertFalse(agenceRepository.existsById(saved.getIdAgence()));
        System.out.println("Agence supprimée, existsById = false");
    }

    // ---------------------------------------------------------------
    // Partie 3 : ClientRepository
    // ---------------------------------------------------------------
    @Test
    void testClientRepository() {
        Client c1 = new Client();
        c1.setNom("Ben Ali");
        c1.setPrenom("Sami");
        c1.setEmail("sami.benali@example.com");
        c1.setTelephone("20111222");
        c1.setNumPermis("P123456");
        c1.setDateInscription(LocalDate.now());

        Client c2 = new Client();
        c2.setNom("Trabelsi");
        c2.setPrenom("Amel");
        c2.setEmail("amel.trabelsi@example.com");
        c2.setTelephone("55333444");
        c2.setNumPermis("P654321");
        c2.setDateInscription(LocalDate.now());

        Client saved1 = clientRepository.save(c1);
        Client saved2 = clientRepository.save(c2);
        System.out.println("Client 1 : id=" + saved1.getIdClient() + ", " + saved1.getNom() + " " + saved1.getPrenom());
        System.out.println("Client 2 : id=" + saved2.getIdClient() + ", " + saved2.getNom() + " " + saved2.getPrenom());
        assertNotNull(saved1.getIdClient());
        assertNotNull(saved2.getIdClient());

        List<Client> clients = clientRepository.findAll();
        System.out.println("Nombre de clients dans la liste : " + clients.size());
        assertTrue(clients.size() >= 2);

        Optional<Client> trouve = clientRepository.findById(saved1.getIdClient());
        assertTrue(trouve.isPresent());
        assertEquals("Ben Ali", trouve.get().getNom());
        System.out.println("Client retrouvé : " + trouve.get().getNom() + " " + trouve.get().getPrenom());

        assertTrue(clientRepository.existsById(saved1.getIdClient()));
        assertFalse(clientRepository.existsById(999999L));
        System.out.println("existsById(existant) = true, existsById(999999) = false");

        long total = clientRepository.count();
        System.out.println("Total de clients : " + total);
        assertTrue(total >= 2);
    }

    // ---------------------------------------------------------------
    // Partie 4 : VehiculeRepository
    // ---------------------------------------------------------------
    @Test
    void testVehiculeRepository() {
        Vehicule v = new Vehicule();
        v.setImmatriculation("123 TUN 4567");
        v.setMarque("Renault");
        v.setModele("Clio");
        v.setTarifJournalier(new BigDecimal("100"));
        // Optionnel : v.setStatut(StatutVehicule.<UNE_VALEUR_DE_TON_ENUM>);
        // Optionnel : v.setCategorie(CategorieVehicule.<UNE_VALEUR_DE_TON_ENUM>);

        Vehicule saved = vehiculeRepository.save(v);
        System.out.println("Véhicule enregistré : id=" + saved.getIdVehicule()
                + ", " + saved.getMarque() + " " + saved.getModele()
                + ", tarif=" + saved.getTarifJournalier());
        assertNotNull(saved.getIdVehicule());

        List<Vehicule> vehicules = vehiculeRepository.findAll();
        System.out.println("Nombre de véhicules dans la liste : " + vehicules.size());
        assertFalse(vehicules.isEmpty());

        long total = vehiculeRepository.count();
        System.out.println("Total de véhicules : " + total);
        assertTrue(total >= 1);

        // Modification : id renseigné, donc save() fait un SELECT puis un UPDATE
        saved.setTarifJournalier(new BigDecimal("150"));
        vehiculeRepository.save(saved);

        Vehicule modifie = vehiculeRepository.findById(saved.getIdVehicule()).orElseThrow();
        System.out.println("Nouveau tarif : " + modifie.getTarifJournalier());
        assertEquals(0, new BigDecimal("150").compareTo(modifie.getTarifJournalier()));

        vehiculeRepository.deleteById(saved.getIdVehicule());
        assertFalse(vehiculeRepository.existsById(saved.getIdVehicule()));
        System.out.println("Véhicule supprimé, existsById = false");
    }

    // ---------------------------------------------------------------
    // Partie 5 : exercice autonome (Equipement, Maintenance, Contrat)
    // ---------------------------------------------------------------
    @Test
    void testEquipementRepository() {
        Equipement e = new Equipement();
        e.setLibelle("GPS");

        Equipement saved = equipementRepository.save(e);
        System.out.println("Équipement enregistré : id=" + saved.getIdEquipement() + ", libelle=" + saved.getLibelle());
        assertNotNull(saved.getIdEquipement());

        Optional<Equipement> trouve = equipementRepository.findById(saved.getIdEquipement());
        assertTrue(trouve.isPresent());
        assertEquals("GPS", trouve.get().getLibelle());

        assertTrue(equipementRepository.existsById(saved.getIdEquipement()));

        equipementRepository.deleteById(saved.getIdEquipement());
        assertFalse(equipementRepository.existsById(saved.getIdEquipement()));
        System.out.println("Équipement supprimé");
    }

    @Test
    void testMaintenanceRepository() {
        Maintenance m = new Maintenance();
        m.setDateDebut(LocalDate.now());
        m.setDateFin(LocalDate.now().plusDays(2));
        m.setDescription("Vidange et révision");

        Maintenance saved = maintenanceRepository.save(m);
        System.out.println("Maintenance enregistrée : id=" + saved.getIdMaintenance()
                + ", description=" + saved.getDescription());
        assertNotNull(saved.getIdMaintenance());

        List<Maintenance> maintenances = maintenanceRepository.findAll();
        System.out.println("Nombre de maintenances : " + maintenances.size());
        assertFalse(maintenances.isEmpty());

        assertTrue(maintenanceRepository.existsById(saved.getIdMaintenance()));

        maintenanceRepository.deleteById(saved.getIdMaintenance());
        assertFalse(maintenanceRepository.existsById(saved.getIdMaintenance()));
        System.out.println("Maintenance supprimée");
    }

    @Test
    void testContratRepository() {
        Contrat c = new Contrat();
        c.setDateSignature(LocalDate.now());
        c.setMontantTotal(new BigDecimal("450"));
        c.setValide(true);

        Contrat saved = contratRepository.save(c);
        System.out.println("Contrat enregistré : id=" + saved.getIdContrat()
                + ", montant=" + saved.getMontantTotal());
        assertNotNull(saved.getIdContrat());

        Optional<Contrat> trouve = contratRepository.findById(saved.getIdContrat());
        assertTrue(trouve.isPresent());
        assertTrue(trouve.get().isValide());

        assertTrue(contratRepository.existsById(saved.getIdContrat()));

        contratRepository.deleteById(saved.getIdContrat());
        assertFalse(contratRepository.existsById(saved.getIdContrat()));
        System.out.println("Contrat supprimé");
    }

}