package talky.dietcontrol.services.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import talky.dietcontrol.model.dto.recipes.CategoryDto;
import talky.dietcontrol.model.dto.recipes.RecipeDTO;
import talky.dietcontrol.model.entities.Meal;
import talky.dietcontrol.model.entities.Recipe;

import java.util.List;

public interface RecipeService {
    List<Long> findNotAllowedProductsForDiagnose(Long diagnoseId) throws JsonProcessingException;

    List<Recipe> findRecipeWithinRangeAndCategory(List<Long> diagnoseId, Meal meal, String category, List<RecipeDTO> allRecipes);

    Recipe getRecipeById(Long id);

    List<CategoryDto> getCategoriesByRecipeId(Long id);

    List<Recipe> getRecipesByIds(List<Long> ids, Integer limit);

    String dayTimeName(String dayTime);

}
