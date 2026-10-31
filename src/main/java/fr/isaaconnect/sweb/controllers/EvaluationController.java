package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.evaluations.EvaluationDTO;
import fr.isaaconnect.sweb.dto.uploads.ImageURLResponseDTO;
import fr.isaaconnect.sweb.services.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/evaluations")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationService evaluationService;

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

    @GetMapping("/{id}/image")
    public ResponseEntity<List<ImageURLResponseDTO>> getRestaurantImageUrl(@PathVariable UUID id) {
        return ResponseEntity.ok(this.evaluationService.getImages(id).stream()
            .map(s -> ImageURLResponseDTO.builder().url(s).build())
            .toList()
        );
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ImageURLResponseDTO> getRestaurantUploadImageUrl(@PathVariable UUID id) {
        return ResponseEntity.ok(ImageURLResponseDTO.builder()
            .url(this.evaluationService.getUploadImage(id))
            .build()
        );
    }
}
