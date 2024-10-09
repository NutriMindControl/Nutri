package talky.dietcontrol.services.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import talky.dietcontrol.model.dto.dailymenu.DailyMenuDTO;
import talky.dietcontrol.model.dto.dailymenu.MealDTO;
import talky.dietcontrol.model.dto.dailymenu.TotalParamsDTO;
import talky.dietcontrol.model.dto.products.ProductChangeResponseDTO;
import talky.dietcontrol.model.dto.products.ProductDTO;
import talky.dietcontrol.model.dto.recipes.RecipeChangeResponseDTO;
import talky.dietcontrol.model.dto.recipes.RecipeDTO;
import talky.dietcontrol.model.entities.Meal;
import talky.dietcontrol.model.entities.Product;

import java.util.List;
import java.util.function.Function;

public interface ChangeService {

    MealDTO getChangingMeal(String mealType, DailyMenuDTO dailyMenuDTO);

    Meal createAndFillMeal(String mealType, List<ProductDTO> allowedProducts, Long diagnoseId, TotalParamsDTO totalParams, List<RecipeDTO> allRecipes) throws JsonProcessingException;

    ProductDTO getChangingProduct(Long productId, DailyMenuDTO dailyMenuDTO);

    Product createAndFillProduct(List<ProductDTO> allowedProducts, TotalParamsDTO totalParams);

    RecipeDTO getChangingRecipe(Long recipeId, DailyMenuDTO dailyMenuDTO);

    RecipeDTO getRecipeForParams(List<RecipeDTO> filteredRecipes, TotalParamsDTO totalParams);

    ProductChangeResponseDTO createProductChangeResponse(ProductDTO productDTO, TotalParamsDTO totalParams);

    RecipeChangeResponseDTO createRecipeChangeResponse(RecipeDTO recipeDTO, TotalParamsDTO totalParams);

    <T, ID> List<T> filterAllowedItems(List<T> allowedItems, List<T> allItemsInMenu, Function<T, ID> idExtractor);

}