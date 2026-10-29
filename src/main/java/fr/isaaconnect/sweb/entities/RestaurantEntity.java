package fr.isaaconnect.sweb.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Collection;
import java.util.UUID;

@Entity @Builder
@Getter @Setter
@Table(name= "restaurant")
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Length(max = 90)
    private String name;

    @Length(max = 255)
    private String address;

    @OneToMany(mappedBy = "restaurant")
    private Collection<EvaluationEntity> evaluations;
}
