package talky.dietcontrol.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "ingredients_distribution")
public class IngredientsDistribution {

    @EmbeddedId
    private IngredientsDistributionKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    @ToString.Exclude
    private Product ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    @ToString.Exclude
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_unit_id")
    @ToString.Exclude
    private MeasureUnit unit;

    @Column(name = "measure_unit_count")
    private Double measureUnitCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredientsDistribution that = (IngredientsDistribution) o;
        return Objects.equals(id.getIngredientId(), that.getId().getIngredientId()) && Objects.equals(id.getRecipeId(), that.getId().getRecipeId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
