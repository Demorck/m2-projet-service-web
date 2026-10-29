package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.evaluations.EvaluationDTO;
import fr.isaaconnect.sweb.dto.restaurants.AddRequestRestaurantDTO;
import fr.isaaconnect.sweb.dto.restaurants.AddResponseRestaurantDTO;
import fr.isaaconnect.sweb.dto.restaurants.ModifyRestaurantDTO;
import fr.isaaconnect.sweb.dto.restaurants.RestaurantDTO;
import fr.isaaconnect.sweb.services.EvaluationService;
import fr.isaaconnect.sweb.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final EvaluationService evaluationService;

    // RESTAURANTS
    @GetMapping
    public List<RestaurantDTO> getRestaurants() {
        return this.restaurantService.getRestaurants().stream().map(RestaurantDTO::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable UUID id) {
        RestaurantDTO dto = RestaurantDTO.fromEntity(this.restaurantService.getRestaurantById(id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> addRestaurant(
            @Valid @RequestBody AddRequestRestaurantDTO restaurant,
            BindingResult result
    ) {
        if (result.hasErrors()) return buildResponseErrorEntityFromBindingResult(result);

        return ResponseEntity.ok(AddResponseRestaurantDTO.fromEntity(
                this.restaurantService.addRestaurant(
                        restaurant.getName(),
                        restaurant.getAddress()
                )));
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
        RestaurantDTO dto = RestaurantDTO.fromEntity(this.restaurantService.getRestaurantById(id));

        return ResponseEntity.ok(dto.getEvaluations());
    }

    @PostMapping("/{id}/evaluations")
    public ResponseEntity<?> addEvaluationByRestaurantId(
            @PathVariable UUID id,
            @Valid @RequestBody EvaluationDTO evaluation,
            BindingResult result
    ) {
        if (result.hasErrors()) return this.buildResponseErrorEntityFromBindingResult(result);

        this.evaluationService.addEvaluation(
                id,
                evaluation.getAuthor(),
                evaluation.getText(),
                evaluation.getNote()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restaurant_id}/evaluations/{evaluation_id}")
    public ResponseEntity<?> removeEvaluationByRestaurantId(
            @PathVariable UUID restaurant_id,
            @PathVariable String evaluation_id
    ) {
        this.evaluationService.removeEvaluation(restaurant_id, evaluation_id);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> buildResponseErrorEntityFromBindingResult(BindingResult result) {
        List<String> errors = result.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }
}
