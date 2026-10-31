package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.evaluations.AddEvaluationRequestDTO;
import fr.isaaconnect.sweb.dto.evaluations.AddEvaluationResponseDTO;
import fr.isaaconnect.sweb.dto.evaluations.EvaluationDTO;
import fr.isaaconnect.sweb.dto.evaluations.EvaluationIndexDTO;
import fr.isaaconnect.sweb.dto.restaurants.AddRestaurantRequestDTO;
import fr.isaaconnect.sweb.dto.restaurants.AddRestaurantResponseDTO;
import fr.isaaconnect.sweb.dto.restaurants.ModifyRestaurantDTO;
import fr.isaaconnect.sweb.dto.restaurants.RestaurantDTO;
import fr.isaaconnect.sweb.dto.uploads.ImageURLResponseDTO;
import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.services.EvaluationService;
import fr.isaaconnect.sweb.services.RestaurantService;
import fr.isaaconnect.sweb.services.indexes.IndexService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final EvaluationService evaluationService;
    private final IndexService indexService;

    // RESTAURANTS
    @GetMapping
    public List<RestaurantDTO> getRestaurants() {
        return this.restaurantService.getRestaurants()
            .stream()
            .map(entry -> RestaurantDTO.fromEntity(entry, this.restaurantService.noteMean(entry)))
            .toList();
    }

    @PostMapping
    public ResponseEntity<?> addRestaurant(
        @Valid @RequestBody AddRestaurantRequestDTO restaurant
    ) {
        return ResponseEntity.ok(AddRestaurantResponseDTO.fromEntity(
            this.restaurantService.addRestaurant(
                restaurant.getName(),
                restaurant.getAddress()
            )
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable UUID id) {
        RestaurantEntity entity = this.restaurantService.getRestaurantById(id);
        RestaurantDTO dto = RestaurantDTO.fromEntity(entity, this.restaurantService.noteMean(entity));
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> modifyRestaurant(
        @PathVariable UUID id,
        @Valid @RequestBody ModifyRestaurantDTO restaurant
    ) {
        this.restaurantService.modifyRestaurant(id, restaurant.getName(), restaurant.getAddress());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<ImageURLResponseDTO> getRestaurantImageUrl(@PathVariable UUID id) {
        return ResponseEntity.ok(ImageURLResponseDTO.builder()
            .url(this.restaurantService.getImage(id))
            .build()
        );
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ImageURLResponseDTO> getRestaurantUploadImageUrl(@PathVariable UUID id) {
        return ResponseEntity.ok(ImageURLResponseDTO.builder()
            .url(this.restaurantService.getUploadImage(id))
            .build()
        );
    }


    // EVALUATIONS
    @GetMapping("/{id}/evaluations")
    public ResponseEntity<List<EvaluationDTO>> getEvaluationsByRestaurantId(@PathVariable UUID id) {
        return ResponseEntity.ok(
            this.restaurantService.getRestaurantById(id).getEvaluations().stream()
                .map(EvaluationDTO::fromEntity)
                .toList()
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/evaluations")
    public ResponseEntity<?> addEvaluationByRestaurantId(
        @PathVariable UUID id,
        @Valid @RequestBody AddEvaluationRequestDTO evaluation,
        @AuthenticationPrincipal Jwt auth
    ) {
        EvaluationEntity entity = this.evaluationService.addEvaluation(
            id,
            auth.getClaim("name"),
            evaluation.getText(),
            evaluation.getNote(),
            UUID.fromString(auth.getSubject())
        );

        EvaluationIndexDTO indexDTO = EvaluationIndexDTO.fromEntity(entity);
        indexService.indexEvaluation(indexDTO);

        return ResponseEntity.ok(AddEvaluationResponseDTO.fromEntity(entity));
    }

    @DeleteMapping("/{restaurant_id}/evaluations/{evaluation_id}")
    public ResponseEntity<?> removeEvaluationByRestaurantId(
        @PathVariable UUID restaurant_id,
        @PathVariable UUID evaluation_id
    ) {
        this.evaluationService.removeEvaluation(restaurant_id, evaluation_id);
        return ResponseEntity.ok().build();
    }
}
