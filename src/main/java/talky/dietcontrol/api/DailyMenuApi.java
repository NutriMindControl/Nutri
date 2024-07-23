package talky.dietcontrol.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import talky.dietcontrol.config.Constants;
import talky.dietcontrol.model.dto.*;

import java.util.List;

@RequestMapping(Constants.BASE_API_PATH + "/daily-menu")
@Validated
public interface DailyMenuApi {

    @PostMapping
    ResponseEntity<DailyMenuDTO> getDailyMenu(@RequestBody MenuInfoDTO menuInfoDTO) throws JsonProcessingException;

    @PutMapping("/update/{meal_type}")
    ResponseEntity<MealTypeResponseDTO> changeMealType(
            @PathVariable("meal_type") String mealType,
            @RequestParam("diagnose_id") Long diagnoseId,
            @RequestBody DailyMenuDTO menu) throws JsonProcessingException;

    @PutMapping("/update/product/{id}")
    ResponseEntity<ProductChangeResponseDTO> changeProduct(
            @PathVariable("id") Long productId,
            @RequestParam("diagnose_id") Long diagnoseId,
            @RequestBody DailyMenuDTO menu) throws JsonProcessingException;

    @PutMapping("/update/recipe/{id}")
    ResponseEntity<RecipeChangeResponseDTO> changeRecipe(
            @PathVariable("id") Long recipeId,
            @RequestParam("diagnose_id") Long diagnoseId,
            @RequestBody DailyMenuDTO menu) throws JsonProcessingException;

    @GetMapping("/activitylevel")
    ResponseEntity<List<String>> getActivityLevel();

    @PostMapping(value = "/getpdf")
    ResponseEntity<byte[]> getPDF(@RequestBody DailyMenuDTO json);

}
