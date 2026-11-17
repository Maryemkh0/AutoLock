package tn.esprit.autoloc;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.domain.enums.CategorieVehicule;
import tn.esprit.autoloc.domain.enums.ModePaiement;
import tn.esprit.autoloc.domain.enums.RoleEmploye;
import tn.esprit.autoloc.domain.enums.StatutVehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.*;
import tn.esprit.autoloc.web.mapper.*;
import tn.esprit.autoloc.web.validator.ReservationRequestValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration et unitaires pour l'Atelier 7 :
 * - Partie 1.5 : Contrôles et tests d'affectation / désaffectation / transfert
 * - Partie 2 : Mappers MapStruct
 * - Partie 5.4 / 5.5 : Validateur personnalisé ReservationRequestValidator
 * - Partie 5.6 : Tests de validation Bean Validation
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

    @Autowired
    private ContratMapper contratMapper;

    @Autowired
    private PaiementMapper paiementMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ReservationRequestValidator reservationRequestValidator;

    private static Validator validator;
    private static Long idAgence1;
    private static Long idAgence2;
    private static Long idVehicule;
    private static Long idEmploye;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Partie 1.5 : Affectation, Transfert et Désaffectation
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("1.5.1 - Affecter un véhicule à une agence existante")
    void testAffecterVehiculeAAgence() {
        Agence agence1 = new Agence();
        agence1.setNom("Agence Tunis");
        agence1.setVille("Tunis");
        idAgence1 = agenceService.create(agence1).getIdAgence();

        Agence agence2 = new Agence();
        agence2.setNom("Agence Sousse");
        agence2.setVille("Sousse");
        idAgence2 = agenceService.create(agence2).getIdAgence();

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("111TU1111");
        vehicule.setMarque("Peugeot");
        vehicule.setModele("208");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("75.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        idVehicule = vehiculeService.create(vehicule).getIdVehicule();

        // Appel de la méthode demandée dans la Section 1.2 du TP
        vehiculeService.affecterVehiculeAAgence(idVehicule, idAgence1);

        Vehicule rechargé = vehiculeService.findById(idVehicule);
        assertNotNull(rechargé.getAgence());
        assertEquals(idAgence1, rechargé.getAgence().getIdAgence());
    }

    @Test
    @Order(2)
    @DisplayName("1.5.2 - Affectation avec identifiant véhicule ou agence inexistant (404)")
    void testAffectationIdentifiantInexistant() {
        assertThrows(ResourceNotFoundException.class, () ->
                vehiculeService.affecterVehiculeAAgence(99999L, idAgence1));

        assertThrows(ResourceNotFoundException.class, () ->
                vehiculeService.affecterVehiculeAAgence(idVehicule, 99999L));
    }

    @Test
    @Order(3)
    @DisplayName("1.5.3 - Transfert d'un véhicule d'une agence vers une autre puis désaffectation")
    void testTransfertEtDesaffectationVehicule() {
        // Transfert de agence 1 vers agence 2
        vehiculeService.affecterVehiculeAAgence(idVehicule, idAgence2);
        Vehicule transfere = vehiculeService.findById(idVehicule);
        assertEquals(idAgence2, transfere.getAgence().getIdAgence());

        // Désaffectation (Section 1.3)
        vehiculeService.desaffecterVehiculeDeAgence(idVehicule);
        Vehicule desaffecte = vehiculeService.findById(idVehicule);
        assertNull(desaffecte.getAgence(), "La clé étrangère de l'agence doit être NULL après désaffectation");
    }

    @Test
    @Order(4)
    @DisplayName("1.5.4 - Affectation et désaffectation d'un employé (Section 1.4)")
    void testAffectationEtDesaffectationEmploye() {
        Employe employe = new Employe();
        employe.setNom("Ben Salah");
        employe.setPrenom("Karim");
        employe.setRole(RoleEmploye.AGENT);
        idEmploye = employeService.create(employe).getIdEmploye();

        // Affecter
        employeService.affecterEmployeAAgence(idEmploye, idAgence1);
        Employe rechargé = employeService.findById(idEmploye);
        assertNotNull(rechargé.getAgence());
        assertEquals(idAgence1, rechargé.getAgence().getIdAgence());

        // Désaffecter
        employeService.desaffecterEmployeDeAgence(idEmploye);
        Employe sansAgence = employeService.findById(idEmploye);
        assertNull(sansAgence.getAgence(), "L'employé ne doit plus avoir d'agence");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Partie 2 : Mappers MapStruct
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("2.5 - Vérification des Mappers MapStruct (Agence, Vehicule, Client)")
    void testMappersMapStruct() {
        AgenceDTO agenceDTO = AgenceDTO.builder()
                .nom("Agence Bizerte")
                .ville("Bizerte")
                .adresse("Port")
                .telephone("72111222")
                .build();
        Agence agence = agenceMapper.toEntity(agenceDTO);
        assertEquals("Agence Bizerte", agence.getNom());
        AgenceDTO mappedBack = agenceMapper.toDto(agence);
        assertEquals("Bizerte", mappedBack.getVille());

        ClientDTO clientDTO = ClientDTO.builder()
                .nom("Trabelsi")
                .prenom("Ali")
                .email("ali@test.com")
                .numPermis("P123456")
                .build();
        Client client = clientMapper.toEntity(clientDTO);
        assertEquals("Trabelsi", client.getNom());

        VehiculeDTO vehiculeDTO = VehiculeDTO.builder()
                .immatriculation("222TU2222")
                .marque("Toyota")
                .modele("Yaris")
                .categorie(CategorieVehicule.CITADINE)
                .tarifJournalier(new BigDecimal("90.00"))
                .build();
        Vehicule v = vehiculeMapper.toEntity(vehiculeDTO);
        assertEquals("Toyota", v.getMarque());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Partie 5.4 : Validateur Personnalisé ReservationRequestValidator
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("5.4/5.6 - Validateur personnalisé : date de début passée")
    void testValidatorDateDebutPassee() {
        ReservationRequestDTO dto = ReservationRequestDTO.builder()
                .clientId(1L)
                .vehiculeId(1L)
                .dateDebut(LocalDate.now().minusDays(2)) // Passée !
                .dateFin(LocalDate.now().plusDays(2))
                .build();

        Errors errors = new BeanPropertyBindingResult(dto, "reservationRequestDTO");
        reservationRequestValidator.validate(dto, errors);

        assertTrue(errors.hasFieldErrors("dateDebut"));
        assertEquals("reservation.dateDebut.passee", errors.getFieldError("dateDebut").getCode());
    }

    @Test
    @Order(7)
    @DisplayName("5.4/5.6 - Validateur personnalisé : date de fin antérieure ou égale à date de début")
    void testValidatorDatesInvalides() {
        ReservationRequestDTO dto = ReservationRequestDTO.builder()
                .clientId(1L)
                .vehiculeId(1L)
                .dateDebut(LocalDate.now().plusDays(5))
                .dateFin(LocalDate.now().plusDays(3)) // Antérieure !
                .build();

        Errors errors = new BeanPropertyBindingResult(dto, "reservationRequestDTO");
        reservationRequestValidator.validate(dto, errors);

        assertTrue(errors.hasFieldErrors("dateFin"));
        assertEquals("reservation.dates.invalides", errors.getFieldError("dateFin").getCode());
    }

    @Test
    @Order(8)
    @DisplayName("5.4/5.6 - Validateur personnalisé : dates cohérentes -> aucune erreur")
    void testValidatorDatesValides() {
        ReservationRequestDTO dto = ReservationRequestDTO.builder()
                .clientId(1L)
                .vehiculeId(1L)
                .dateDebut(LocalDate.now().plusDays(1))
                .dateFin(LocalDate.now().plusDays(5))
                .build();

        Errors errors = new BeanPropertyBindingResult(dto, "reservationRequestDTO");
        reservationRequestValidator.validate(dto, errors);

        assertFalse(errors.hasErrors(), "Aucune erreur attendue pour des dates cohérentes");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Partie 5.6 : Tests Bean Validation sur les DTOs
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("5.6.1 - Client avec email invalide : rejeté par Bean Validation")
    void testValidationClientEmailInvalide() {
        ClientDTO dto = ClientDTO.builder()
                .nom("Gharbi")
                .prenom("Fatma")
                .email("email-invalide") // Email invalide !
                .build();

        Set<ConstraintViolation<ClientDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "L'email invalide doit générer une violation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @Order(10)
    @DisplayName("5.6.2 - Véhicule sans immatriculation : rejeté par Bean Validation")
    void testValidationVehiculeSansImmatriculation() {
        VehiculeDTO dto = VehiculeDTO.builder()
                .immatriculation("") // Vide !
                .marque("Volkswagen")
                .modele("Golf")
                .categorie(CategorieVehicule.BERLINE)
                .tarifJournalier(new BigDecimal("120.00"))
                .build();

        Set<ConstraintViolation<VehiculeDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("immatriculation")));
    }

    @Test
    @Order(11)
    @DisplayName("5.6.3 - Paiement avec montant nul ou négatif : rejeté par Bean Validation")
    void testValidationPaiementMontantNegatif() {
        PaiementDTO dto = PaiementDTO.builder()
                .montant(new BigDecimal("-10.00")) // Négatif !
                .modePaiement(ModePaiement.CARTE)
                .contratId(1L)
                .build();

        Set<ConstraintViolation<PaiementDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("montant")));
    }

    @Test
    @Order(12)
    @DisplayName("5.6.4 - Réservation sans dates : rejeté par Bean Validation")
    void testValidationReservationSansDates() {
        ReservationRequestDTO dto = ReservationRequestDTO.builder()
                .clientId(1L)
                .vehiculeId(1L)
                .dateDebut(null) // Nulle !
                .dateFin(null)
                .build();

        Set<ConstraintViolation<ReservationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateDebut")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateFin")));
    }
}
