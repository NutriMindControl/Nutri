package talky.dietcontrol.services.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import talky.dietcontrol.model.dto.Meal;
import talky.dietcontrol.model.dto.RecipeDTO;

import java.util.List;

public interface RecipeService {
    List<RecipeDTO> findAllowedRecipesForDiagnose(Long diagnoseId) throws JsonProcessingException;

    List<RecipeDTO> findRecipeWithinRangeAndCategory(List<RecipeDTO> allowedRecipes, Meal meal, String category);
}
