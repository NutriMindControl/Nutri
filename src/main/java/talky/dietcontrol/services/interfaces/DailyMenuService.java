package talky.dietcontrol.services.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import talky.dietcontrol.model.dto.dailymenu.DailyMenuDTO;
import talky.dietcontrol.model.dto.dailymenu.MealTypeResponseDTO;
import talky.dietcontrol.model.dto.dailymenu.MenuInfoDTO;
import talky.dietcontrol.model.dto.products.ProductChangeResponseDTO;
import talky.dietcontrol.model.dto.recipes.RecipeChangeResponseDTO;
import talky.dietcontrol.model.dto.recipes.RecipeDTO;

import java.util.List;

public interface DailyMenuService {
    ResponseEntity<RecipeDTO> getRecipeById(Long id) throws JsonProcessingException;

    ResponseEntity<DailyMenuDTO> getDailyMenu(MenuInfoDTO menuInfoDTO) throws JsonProcessingException;


    ResponseEntity<MealTypeResponseDTO> changeMealType(String mealType, Long diagnoseId, DailyMenuDTO dailyMenuDTO) throws JsonProcessingException;

    ResponseEntity<ProductChangeResponseDTO> changeProduct(Long productId, Long diagnoseId, DailyMenuDTO menu);

    ResponseEntity<RecipeChangeResponseDTO> changeRecipe(Long recipeId, Long diagnoseId, DailyMenuDTO menu) throws JsonProcessingException;

    ResponseEntity<List<String>> getActivityLevels();

    ResponseEntity<byte[]> getPdf(DailyMenuDTO menu);
}
