package talky.dietcontrol.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MealDTO implements BaseDTOWithNutrientsMethods {
    List<RecipeDTO> recipes = new ArrayList<>();
    List<ProductDTO> products = new ArrayList<>();
    Double fats;
    Double carbohydrates;
    Double proteins;
    Double calories;
}
