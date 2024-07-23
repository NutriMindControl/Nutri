package talky.dietcontrol.services.interfaces;

import talky.dietcontrol.model.dto.*;
import talky.dietcontrol.model.entities.Product;

import java.util.List;
import java.util.function.Function;

public interface ChangeService {

    MealDTO getChangingMeal(String mealType, DailyMenuDTO dailyMenuDTO);

    Meal createAndFillMeal(String mealType, List<ProductDTO> allowedProducts, List<RecipeDTO> allowedRecipes, TotalParamsDTO totalParams);

    ProductDTO getChangingProduct(Long productId, DailyMenuDTO dailyMenuDTO);

    Product createAndFillProduct(List<ProductDTO> allowedProducts, TotalParamsDTO totalParams);

    RecipeDTO getChangingRecipe(Long recipeId, DailyMenuDTO dailyMenuDTO);

    RecipeDTO getRecipeForParams(List<RecipeDTO> filteredRecipes, TotalParamsDTO totalParams);

    ProductChangeResponseDTO createProductChangeResponse(ProductDTO productDTO, TotalParamsDTO totalParams);

    RecipeChangeResponseDTO createRecipeChangeResponse(RecipeDTO recipeDTO, TotalParamsDTO totalParams);

    <T, ID> List<T> filterAllowedItems(List<T> allowedItems, List<T> allItemsInMenu, Function<T, ID> idExtractor);

}