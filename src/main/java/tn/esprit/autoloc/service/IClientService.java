package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.web.dto.ClientDTO;

import java.util.List;

public interface IClientService {

    // ─── Atelier 7 : Méthodes DTO (Section 3.1 & 3.4) ─────────────────────────
    ClientDTO creer(ClientDTO dto);
    ClientDTO obtenirParId(Long id);
    List<ClientDTO> listerTous();

    // ─── Méthodes héritées Atelier 4 (compatibilité tests) ───────────────────
    Client create(Client entite);
    Client findById(Long id);
    List<Client> findAll();
    Client update(Long id, Client entite);
    void deleteById(Long id);
}
