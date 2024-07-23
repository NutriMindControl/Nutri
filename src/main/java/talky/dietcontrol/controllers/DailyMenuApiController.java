package talky.dietcontrol.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import talky.dietcontrol.api.DailyMenuApi;
import talky.dietcontrol.model.dto.*;
import talky.dietcontrol.services.interfaces.DailyMenuService;

import java.util.List;


@CrossOrigin(maxAge = 1440)
@RestController
public class DailyMenuApiController implements DailyMenuApi {
    private final DailyMenuService service;

    public DailyMenuApiController(DailyMenuService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<DailyMenuDTO> getDailyMenu(MenuInfoDTO menuInfoDTO) throws JsonProcessingException {
        return service.getDailyMenu(menuInfoDTO);
    }

    @Override
    public ResponseEntity<MealTypeResponseDTO> changeMealType(String mealType, Long diagnoseId, DailyMenuDTO menu) throws JsonProcessingException {
        return service.changeMealType(mealType, diagnoseId, menu);
    }

    @Override
    public ResponseEntity<ProductChangeResponseDTO> changeProduct(Long productId, Long diagnoseId, DailyMenuDTO menu) throws JsonProcessingException {
        return service.changeProduct(productId, diagnoseId, menu);
    }

    @Override
    public ResponseEntity<RecipeChangeResponseDTO> changeRecipe(Long recipeId, Long diagnoseId, DailyMenuDTO menu) throws JsonProcessingException {
        return service.changeRecipe(recipeId, diagnoseId, menu);
    }

    @Override
    public ResponseEntity<List<String>> getActivityLevel() {
        return service.getActivityLevels();
    }

    @Override
    public ResponseEntity<byte[]> getPDF(DailyMenuDTO menu) {
        return service.getPdf(menu);
    }


}
