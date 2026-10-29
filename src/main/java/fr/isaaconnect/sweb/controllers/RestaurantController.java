package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.restaurants.RestaurantDTO;
import fr.isaaconnect.sweb.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController
{
    private final RestaurantService service;

    @GetMapping
    public List<RestaurantDTO> getRestaurants() {
        return this.service.getRestaurants().stream().map(RestaurantDTO::fromEntity).toList();
    }

    @PostMapping
    public ResponseEntity<?> addRestaurant(@Valid @RequestBody RestaurantDTO restaurant, BindingResult result) {
        if (result.hasErrors()) return buildResponseErrorEntity(result);

        this.service.addRestaurant(restaurant.getName(), restaurant.getAddress());
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> buildResponseErrorEntity(BindingResult result) {
        List<String> errors = result.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }
}
