package com.example.autoloc.domain;

import com.example.autoloc.domain.enums.ModePaiement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "paiement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaiement;

    private BigDecimal montant;

    private LocalDate datePaiement;

    private ModePaiement modePaiement;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrat")
    private Contrat contrat;}