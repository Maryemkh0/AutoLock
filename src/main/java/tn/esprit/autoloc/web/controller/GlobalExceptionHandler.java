package tn.esprit.autoloc.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestionnaire global des exceptions HTTP.
 * Atelier 7 : Ajout de la gestion de MethodArgumentNotValidException
 * pour les erreurs de Bean Validation (@Valid sur les DTOs).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 - Ressource introuvable */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statut", HttpStatus.NOT_FOUND.value());
        body.put("message", ex.getMessage());
        body.put("horodatage", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /** 400 - Argument illégal (validations métier dans les services) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statut", HttpStatus.BAD_REQUEST.value());
        body.put("message", ex.getMessage());
        body.put("horodatage", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 400 - Validation Bean Validation échouée (@Valid sur les DTOs).
     * Retourne la liste de tous les champs invalides avec leurs messages d'erreur.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Collecte tous les messages d'erreur par champ
        List<String> erreurs = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        // Détail champ → message pour le débogage
        Map<String, String> champsErreurs = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        // En cas de doublon de clé (plusieurs contraintes sur un champ), on concatène
                        (m1, m2) -> m1 + "; " + m2
                ));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statut", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Validation échouée : " + erreurs.size() + " erreur(s)");
        body.put("erreurs", champsErreurs);
        body.put("horodatage", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
