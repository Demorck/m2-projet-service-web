package fr.isaaconnect.sweb.entities;

import jakarta.persistence.*;
import jakarta.validation.Constraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Entity
@Getter @Setter
@Table(name= "restaurant")
public class RestaurantEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Length(max = 90)
    private String name;

    @Length(max = 255)
    private String address;
}
