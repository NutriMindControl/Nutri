package talky.dietcontrol.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import talky.dietcontrol.model.dto.dailymenu.BaseDTOWithNutrientsMethods;

import java.util.List;

@NoArgsConstructor
@Data
@ToString
@Entity
@Builder
@Table(name = "recipes")
@AllArgsConstructor
public class Recipe implements BaseDTOWithNutrientsMethods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Column(name = "cook_time_mins")
    private Integer cookTimeMins;

    @Column(name = "prep_time_mins")
    private String prepTimeMins;

    private Double kilocalories;

    private Double proteins;

    private Double fats;

    private Double carbohydrates;


    @OneToMany(mappedBy = "recipe", orphanRemoval = true, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Step> steps;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe", orphanRemoval = true)
    @ToString.Exclude
    private List<IngredientsDistribution> ingredientsDistributions;


    @ManyToMany(mappedBy = "recipes")
    @ToString.Exclude
    private List<Category> categories;

    @Override
    public Double getCalories() {
        return kilocalories;
    }
}
