package talky.dietcontrol.services.interfaces;

import talky.dietcontrol.model.dto.Meal;
import talky.dietcontrol.model.dto.MealDTO;
import talky.dietcontrol.model.dto.ProductDTO;
import talky.dietcontrol.model.dto.RecipeDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAllowedProductsForSelfConsumptionForDiagnose(Long diagnoseId);

    void removeUsedProducts(List<ProductDTO> mutableList, List<RecipeDTO> mutableListRecipes, MealDTO breakfastMeals);

    void fillMealWithProducts(Meal meal, List<ProductDTO> allowedProducts);
}
