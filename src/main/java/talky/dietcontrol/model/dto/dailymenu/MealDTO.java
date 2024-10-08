package talky.dietcontrol.model.dto.dailymenu;

import lombok.Data;
import lombok.NoArgsConstructor;
import talky.dietcontrol.model.dto.products.ProductDTO;
import talky.dietcontrol.model.dto.recipes.RecipeDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MealDTO implements BaseDTOWithNutrientsMethods {
    List<RecipeDTO> recipes = new ArrayList<>();
    List<ProductDTO> products = new ArrayList<>();
    Double fats;
    Double carbohydrates;
    Double proteins;
    Double calories;

    public MealDTO(Double fats, Double carbohydrates, Double proteins, Double calories) {
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
        this.calories = calories;
    }
}
