package talky.dietcontrol.model.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class IngredientsDistributionKey implements Serializable {
    @Column(name = "recipe_id")
    private Long recipeId;

    @Column(name = "ingredient_id")
    private Long ingredientId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredientsDistributionKey that = (IngredientsDistributionKey) o;
        return getRecipeId() == that.getRecipeId() && getIngredientId() == that.getIngredientId();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
