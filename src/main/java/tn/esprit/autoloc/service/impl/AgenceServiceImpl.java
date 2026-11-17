package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IAgenceService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IEmployeRepository employeRepository;

    @Override
    public Agence create(Agence agence) {
        if (agence.getIdAgence() != null) {
            throw new IllegalArgumentException("Une nouvelle agence ne doit pas avoir d'identifiant");
        }
        validerAgence(agence);
        return agenceRepository.save(agence);
    }

    @Override
    public Agence findById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    @Override
    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    @Override
    public Agence update(Long id, Agence agence) {
        Agence existante = findById(id);
        validerAgence(agence);

        existante.setNom(agence.getNom());
        existante.setVille(agence.getVille());
        existante.setAdresse(agence.getAdresse());
        existante.setTelephone(agence.getTelephone());

        return agenceRepository.save(existante);
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id);
        }
        agenceRepository.deleteById(id);
    }

    // ─── Méthodes d'affectation (Atelier 7) ──────────────────────────────────

    @Override
    public Agence affecterVehicule(Long idAgence, Long idVehicule) {
        Agence agence = findById(idAgence);
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVehicule));

        vehicule.setAgence(agence);
        vehiculeRepository.save(vehicule);
        return agence;
    }

    @Override
    public Agence retirerVehicule(Long idAgence, Long idVehicule) {
        // Vérifie que l'agence existe
        findById(idAgence);
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVehicule));

        if (vehicule.getAgence() == null || !vehicule.getAgence().getIdAgence().equals(idAgence)) {
            throw new IllegalArgumentException(
                    "Le véhicule " + idVehicule + " n'appartient pas à l'agence " + idAgence);
        }
        vehicule.setAgence(null);
        vehiculeRepository.save(vehicule);
        return findById(idAgence);
    }

    @Override
    public Agence affecterEmploye(Long idAgence, Long idEmploye) {
        Agence agence = findById(idAgence);
        Employe employe = employeRepository.findById(idEmploye)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", idEmploye));

        employe.setAgence(agence);
        employeRepository.save(employe);
        return agence;
    }

    // ─── Validation ───────────────────────────────────────────────────────────

    private void validerAgence(Agence agence) {
        if (agence.getNom() == null || agence.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'agence est obligatoire");
        }
    }
}

