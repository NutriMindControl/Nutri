package talky.dietcontrol.services.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import talky.dietcontrol.model.dto.*;

import java.util.List;

public interface DailyMenuService {
    ResponseEntity<TalkyRecipeDTO> getRecipeById(Long id) throws JsonProcessingException;

    ResponseEntity<DailyMenuDTO> getDailyMenu(MenuInfoDTO menuInfoDTO) throws JsonProcessingException;


    ResponseEntity<MealTypeResponseDTO> changeMealType(String mealType, Long diagnoseId, DailyMenuDTO dailyMenuDTO) throws JsonProcessingException;

    ResponseEntity<ProductChangeResponseDTO> changeProduct(Long productId, Long diagnoseId, DailyMenuDTO menu);

    ResponseEntity<RecipeChangeResponseDTO> changeRecipe(Long recipeId, Long diagnoseId, DailyMenuDTO menu) throws JsonProcessingException;

    ResponseEntity<List<String>> getActivityLevels();

    ResponseEntity<byte[]> getPdf(DailyMenuDTO menu);
}
