package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.evaluations.EvaluationDTO;
import fr.isaaconnect.sweb.services.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/evaluations")
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationService evaluationService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<List<EvaluationDTO>> getEvaluationsByAuthor(
        Authentication auth
    ) {
        return ResponseEntity.ok(
            this.evaluationService.getEvaluationByAuthor(UUID.fromString(auth.getName())).stream()
                .map(EvaluationDTO::fromEntity)
                .toList()
        );
    }
}
