package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.evaluations.EvaluationDTO;
import fr.isaaconnect.sweb.dto.restaurants.ModifyRestaurantDTO;
import fr.isaaconnect.sweb.dto.restaurants.RestaurantDTO;
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
public class RestaurantController
{
    private final RestaurantService service;

    // RESTAURANTS
    @GetMapping
    public List<RestaurantDTO> getRestaurants() {
        return this.service.getRestaurants().stream().map(RestaurantDTO::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable UUID id) {
        RestaurantDTO dto = RestaurantDTO.fromEntity(this.service.getRestaurantById(id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> addRestaurant(@Valid @RequestBody RestaurantDTO restaurant, BindingResult result) {
        if (result.hasErrors()) return buildResponseErrorEntityFromBindingResult(result);

        this.service.addRestaurant(restaurant.getName(), restaurant.getAddress());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> modifyRestaurant(
            @PathVariable UUID id,
            @Valid @RequestBody ModifyRestaurantDTO restaurant,
            BindingResult result
    ) {
        if (result.hasErrors()) return buildResponseErrorEntityFromBindingResult(result);

        this.service.modifyRestaurant(id, restaurant.getName(), restaurant.getAddress());
        return ResponseEntity.ok().build();
    }


    // EVALUATIONS
    @GetMapping("/{id}/evaluations")
    public ResponseEntity<List<EvaluationDTO>> getEvaluationsByRestaurantId(@PathVariable UUID id) {
        RestaurantDTO dto = RestaurantDTO.fromEntity(this.service.getRestaurantById(id));

        return ResponseEntity.ok(dto.getEvaluations());
    }

    private ResponseEntity<?> buildResponseErrorEntityFromBindingResult(BindingResult result) {
        List<String> errors = result.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }
}
