package fr.isaaconnect.sweb.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@Entity @Builder
@Getter
@Setter
@Table(name= "evaluation")
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationEntity
{
    @Id
    @GeneratedValue
    private UUID id;

    @Length(max = 50)
    private String author;

    @Length(max = 255)
    private String text;

    @Range(min = 0, max = 3)
    private int note;

    @ManyToOne
    private RestaurantEntity restaurant;
}
