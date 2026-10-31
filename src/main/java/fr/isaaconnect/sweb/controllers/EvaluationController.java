package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.evaluations.EvaluationDTO;
import fr.isaaconnect.sweb.dto.uploads.ImageURLResponseDTO;
import fr.isaaconnect.sweb.services.EvaluationService;
import fr.isaaconnect.sweb.services.indexes.IndexService;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/evaluations")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationService evaluationService;
    private final IndexService indexService;

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

    @GetMapping("/search")
    public ResponseEntity<List<EvaluationDTO>> searchEvaluation(
        @RequestParam("q") String query
    ) {
        List<Document> results = this.indexService.getIndexEvaluation(query);

        return ResponseEntity.ok(
            results.stream().map(EvaluationDTO::fromLuceneDocument).toList()
        );
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<List<ImageURLResponseDTO>> getEvaluationImageUrl(@PathVariable UUID id) {
        return ResponseEntity.ok(this.evaluationService.getImages(id).stream()
            .map(s -> ImageURLResponseDTO.builder().url(s).build())
            .toList()
        );
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ImageURLResponseDTO> getEvaluationUploadImageUrl(
        @PathVariable UUID id,
        @AuthenticationPrincipal Jwt auth
    ) {
        return ResponseEntity.ok(ImageURLResponseDTO.builder()
            .url(this.evaluationService.getUploadImage(id, UUID.fromString(auth.getSubject())))
            .build()
        );
    }
}
