package tn.esprit.autoloc;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.domain.enums.CategorieVehicule;
import tn.esprit.autoloc.domain.enums.RoleEmploye;
import tn.esprit.autoloc.domain.enums.StatutVehicule;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.*;
import tn.esprit.autoloc.web.mapper.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour l'Atelier 7 :
 * - DTOs et mapping MapStruct
 * - Méthodes d'affectation (véhicule/employé → agence)
 * - Bean Validation sur les DTOs
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Atelier7Test {

    @Autowired
    private IAgenceService agenceService;

    @Autowired
    private IVehiculeService vehiculeService;

    @Autowired
    private IEmployeService employeService;

    @Autowired
    private IClientService clientService;

    @Autowired
    private AgenceMapper agenceMapper;

    @Autowired
    private VehiculeMapper vehiculeMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private EmployeMapper employeMapper;

    // IDs partagés entre les tests
    private static Long idAgence;
    private static Long idVehicule;
    private static Long idEmploye;

    // ─── Test 1 : Mapping AgenceDTO <-> Agence ────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Test 1 - Mapping AgenceDTO vers Agence (MapStruct)")
    void testMappingAgenceDto() {
        AgenceDTO dto = new AgenceDTO();
        dto.setNom("Agence Tunis Centre");
        dto.setVille("Tunis");
        dto.setAdresse("Avenue Habib Bourguiba");
        dto.setTelephone("+21671234567");

        Agence agence = agenceMapper.toEntity(dto);

        assertNotNull(agence, "L'entité mappée ne doit pas être null");
        assertEquals("Agence Tunis Centre", agence.getNom());
        assertEquals("Tunis", agence.getVille());
        assertNull(agence.getIdAgence(), "L'id ne doit pas être mappé depuis le DTO");

        // Mapping inverse
        AgenceDTO dtoRetour = agenceMapper.toDto(agence);
        assertEquals(dto.getNom(), dtoRetour.getNom());
        assertEquals(dto.getVille(), dtoRetour.getVille());

        System.out.println("✅ Mapping AgenceDTO <-> Agence fonctionne correctement");
    }

    // ─── Test 2 : Mapping VehiculeDTO <-> Vehicule ───────────────────────────

    @Test
    @Order(2)
    @DisplayName("Test 2 - Mapping VehiculeDTO vers Vehicule (MapStruct)")
    void testMappingVehiculeDto() {
        VehiculeDTO dto = new VehiculeDTO();
        dto.setImmatriculation("123TU1234");
        dto.setMarque("Peugeot");
        dto.setModele("208");
        dto.setCategorie(CategorieVehicule.CITADINE);
        dto.setTarifJournalier(new BigDecimal("80.00"));
        dto.setKilometrage(15000);
        dto.setStatut(StatutVehicule.DISPONIBLE);

        Vehicule vehicule = vehiculeMapper.toEntity(dto);

        assertNotNull(vehicule);
        assertEquals("123TU1234", vehicule.getImmatriculation());
        assertEquals("Peugeot", vehicule.getMarque());
        assertEquals(CategorieVehicule.CITADINE, vehicule.getCategorie());
        assertEquals(0, vehicule.getTarifJournalier().compareTo(new BigDecimal("80.00")));
        assertNull(vehicule.getIdVehicule(), "L'id ne doit pas être mappé depuis le DTO");
        assertNull(vehicule.getAgence(), "L'agence est ignorée dans le mapper (gérée dans le service)");

        System.out.println("✅ Mapping VehiculeDTO <-> Vehicule fonctionne correctement");
    }

    // ─── Test 3 : Affectation d'un véhicule à une agence ────────────────────

    @Test
    @Order(3)
    @DisplayName("Test 3 - Affecter un véhicule à une agence")
    void testAffecterVehiculeAgence() {
        // Créer agence
        Agence agence = new Agence();
        agence.setNom("Agence Sfax");
        agence.setVille("Sfax");
        Agence agenceSauvee = agenceService.create(agence);
        idAgence = agenceSauvee.getIdAgence();

        // Créer véhicule sans agence
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("456SF4567");
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("70.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        Vehicule vehiculeSauve = vehiculeService.create(vehicule);
        idVehicule = vehiculeSauve.getIdVehicule();

        assertNull(vehiculeSauve.getAgence(), "Le véhicule ne doit pas avoir d'agence initialement");

        // Affecter
        agenceService.affecterVehicule(idAgence, idVehicule);

        // Vérifier l'affectation
        Vehicule vehiculeAffecte = vehiculeService.findById(idVehicule);
        assertNotNull(vehiculeAffecte.getAgence(), "Le véhicule doit avoir une agence après affectation");
        assertEquals(idAgence, vehiculeAffecte.getAgence().getIdAgence());

        System.out.println("✅ Affectation véhicule → agence fonctionne : véhicule "
                + idVehicule + " → agence " + idAgence);
    }

    // ─── Test 4 : Affectation d'un employé à une agence ─────────────────────

    @Test
    @Order(4)
    @DisplayName("Test 4 - Affecter un employé à une agence")
    void testAffecterEmployeAgence() {
        // Créer employé sans agence
        Employe employe = new Employe();
        employe.setNom("Jebali");
        employe.setPrenom("Mohamed");
        employe.setRole(RoleEmploye.AGENT);
        Employe employeSauve = employeService.create(employe);
        idEmploye = employeSauve.getIdEmploye();

        assertNull(employeSauve.getAgence(), "L'employé ne doit pas avoir d'agence initialement");

        // Affecter à l'agence créée au test 3
        agenceService.affecterEmploye(idAgence, idEmploye);

        // Vérifier
        Employe employeAffecte = employeService.findById(idEmploye);
        assertNotNull(employeAffecte.getAgence(), "L'employé doit avoir une agence après affectation");
        assertEquals(idAgence, employeAffecte.getAgence().getIdAgence());

        System.out.println("✅ Affectation employé → agence fonctionne : employé "
                + idEmploye + " → agence " + idAgence);
    }

    // ─── Test 5 : Validation DTO - champs obligatoires ───────────────────────

    @Test
    @Order(5)
    @DisplayName("Test 5 - Validation DTO : VehiculeDTO avec tarif négatif → exception")
    void testValidationVehiculeDto() {
        VehiculeDTO dto = new VehiculeDTO();
        dto.setImmatriculation("789TU7890");
        dto.setMarque("Fiat");
        dto.setModele("Punto");
        dto.setCategorie(CategorieVehicule.CITADINE);
        dto.setTarifJournalier(new BigDecimal("-50.00"));  // INVALIDE

        // Mapper vers l'entité (le mapper ne valide pas, c'est @Valid dans le controller)
        Vehicule vehicule = vehiculeMapper.toEntity(dto);

        // La validation métier dans le service doit rejeter le tarif négatif
        assertThrows(IllegalArgumentException.class,
                () -> vehiculeService.create(vehicule),
                "Le service doit rejeter un tarif journalier négatif");

        System.out.println("✅ Validation tarif négatif : IllegalArgumentException levée correctement");
    }

    // ─── Test 6 : Mapping ClientDTO ──────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("Test 6 - Mapping ClientDTO vers Client et création via service")
    void testMappingClientDtoEtCreation() {
        ClientDTO dto = new ClientDTO();
        dto.setNom("Mejri");
        dto.setPrenom("Sonia");
        dto.setEmail("sonia.mejri@email.com");
        dto.setTelephone("+21698765432");
        dto.setNumPermis("TN12345");

        Client client = clientMapper.toEntity(dto);

        assertNotNull(client);
        assertEquals("Mejri", client.getNom());
        assertEquals("sonia.mejri@email.com", client.getEmail());
        assertNull(client.getIdClient());

        // Créer via service
        Client clientSauve = clientService.create(client);
        assertNotNull(clientSauve.getIdClient());

        // Mapper en retour
        ClientDTO dtoRetour = clientMapper.toDto(clientSauve);
        assertEquals("sonia.mejri@email.com", dtoRetour.getEmail());

        System.out.println("✅ Mapping ClientDTO et création : id=" + clientSauve.getIdClient());
    }
}
