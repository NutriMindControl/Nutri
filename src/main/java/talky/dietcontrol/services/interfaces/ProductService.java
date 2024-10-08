package talky.dietcontrol.services.interfaces;


import talky.dietcontrol.model.dto.dailymenu.MealDTO;
import talky.dietcontrol.model.dto.products.ProductDTO;
import talky.dietcontrol.model.entities.Meal;
import talky.dietcontrol.model.entities.Recipe;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAllowedProductsForSelfConsumptionForDiagnose(Long diagnoseId);

    void removeUsedProducts(List<ProductDTO> mutableList, List<Recipe> mutableListRecipes, MealDTO breakfastMeals);

    void fillMealWithProducts(Meal meal, List<ProductDTO> allowedProducts);
}
