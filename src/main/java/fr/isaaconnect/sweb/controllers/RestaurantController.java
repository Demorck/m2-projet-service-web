package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.errors.FieldValidationErrorDTO;
import fr.isaaconnect.sweb.dto.errors.FieldErrorDTO;
import fr.isaaconnect.sweb.dto.evaluations.AddEvaluationRequestDTO;
import fr.isaaconnect.sweb.dto.evaluations.AddEvaluationResponseDTO;
import fr.isaaconnect.sweb.dto.evaluations.EvaluationDTO;
import fr.isaaconnect.sweb.dto.restaurants.AddRestaurantRequestDTO;
import fr.isaaconnect.sweb.dto.restaurants.AddRestaurantResponseDTO;
import fr.isaaconnect.sweb.dto.restaurants.ModifyRestaurantDTO;
import fr.isaaconnect.sweb.dto.restaurants.RestaurantDTO;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.services.EvaluationService;
import fr.isaaconnect.sweb.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final EvaluationService evaluationService;

    // RESTAURANTS
    @GetMapping
    public List<RestaurantDTO> getRestaurants() {
        return this.restaurantService.getRestaurants()
                .stream()
                .map(entry -> RestaurantDTO.fromEntity(entry, this.restaurantService.noteMean(entry)))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable UUID id) {
        RestaurantEntity entity = this.restaurantService.getRestaurantById(id);
        RestaurantDTO dto = RestaurantDTO.fromEntity(entity, this.restaurantService.noteMean(entity));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> addRestaurant(
            @Valid @RequestBody AddRestaurantRequestDTO restaurant,
            BindingResult result
    ) {
        if (result.hasErrors()) return buildResponseErrorEntityFromBindingResult(result);

        return ResponseEntity.ok(AddRestaurantResponseDTO.fromEntity(
            this.restaurantService.addRestaurant(
                    restaurant.getName(),
                    restaurant.getAddress()
            )
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> modifyRestaurant(
            @PathVariable UUID id,
            @Valid @RequestBody ModifyRestaurantDTO restaurant,
            BindingResult result
    ) {
        if (result.hasErrors()) return buildResponseErrorEntityFromBindingResult(result);

        this.restaurantService.modifyRestaurant(id, restaurant.getName(), restaurant.getAddress());
        return ResponseEntity.ok().build();
    }


    // EVALUATIONS
    @GetMapping("/{id}/evaluations")
    public ResponseEntity<List<EvaluationDTO>> getEvaluationsByRestaurantId(@PathVariable UUID id) {
        RestaurantEntity entity = this.restaurantService.getRestaurantById(id);
        RestaurantDTO dto = RestaurantDTO.fromEntity(entity, this.restaurantService.noteMean(entity));

        return ResponseEntity.ok(dto.getEvaluations());
    }

    @PostMapping("/{id}/evaluations")
    public ResponseEntity<?> addEvaluationByRestaurantId(
            @PathVariable UUID id,
            @Valid @RequestBody AddEvaluationRequestDTO evaluation,
            BindingResult result
    ) {
        if (result.hasErrors()) return this.buildResponseErrorEntityFromBindingResult(result);


        return ResponseEntity.ok(AddEvaluationResponseDTO.fromEntity(
            this.evaluationService.addEvaluation(
                id,
                evaluation.getAuthor(),
                evaluation.getText(),
                evaluation.getNote()
            )
        ));
    }

    @DeleteMapping("/{restaurant_id}/evaluations/{evaluation_id}")
    public ResponseEntity<?> removeEvaluationByRestaurantId(
            @PathVariable UUID restaurant_id,
            @PathVariable UUID evaluation_id
    ) {
        this.evaluationService.removeEvaluation(restaurant_id, evaluation_id);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<FieldValidationErrorDTO> buildResponseErrorEntityFromBindingResult(BindingResult result) {
        List<FieldErrorDTO> errors = result.getFieldErrors()
                .stream()
                .map(FieldErrorDTO::fromFieldError)
                .toList();

        return ResponseEntity.badRequest().body(FieldValidationErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Fields provide are wrong")
                .fieldErrors(errors)
                .build()
        );
    }
}
