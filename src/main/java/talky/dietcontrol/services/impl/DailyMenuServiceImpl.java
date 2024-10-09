package talky.dietcontrol.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import talky.dietcontrol.exceptions.NotFoundException;
import talky.dietcontrol.model.dto.dailymenu.*;
import talky.dietcontrol.model.dto.products.ProductChangeResponseDTO;
import talky.dietcontrol.model.dto.products.ProductDTO;
import talky.dietcontrol.model.dto.recipes.RecipeChangeResponseDTO;
import talky.dietcontrol.model.dto.recipes.RecipeDTO;
import talky.dietcontrol.model.dto.user.PhysicalActivityLevel;
import talky.dietcontrol.model.entities.Meal;
import talky.dietcontrol.model.entities.MealType;
import talky.dietcontrol.model.entities.Product;
import talky.dietcontrol.model.entities.Recipe;
import talky.dietcontrol.repository.RecipeRepository;
import talky.dietcontrol.services.interfaces.CalorieCalculatorService;
import talky.dietcontrol.services.interfaces.DailyMenuService;
import talky.dietcontrol.services.interfaces.ProductService;
import talky.dietcontrol.services.interfaces.RecipeService;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static talky.dietcontrol.services.impl.ChangeServiceImpl.fillTotalParams;

@Slf4j
@AllArgsConstructor
@Service
public class DailyMenuServiceImpl implements DailyMenuService {
    public static final double CARBOHYDRATES_COEF = 0.4 / 4.1;
    public static final double FATS_COEF = 0.3 / 9.3;
    public static final double PROTEINS_COEF = 0.3 / 4.1;
    public static final double PLUS_FIVE_PERCENT = 1.1;
    public static final double MINUS_FIVE_PERCENT = 0.9;


    private final RestTemplate restTemplate;

    private final ModelMapper modelMapper;

    private final RecipeService recipeService;
    private final ProductService productService;
    private final CalorieCalculatorService dailyCalorieNeeds;
    private final ChangeServiceImpl changeService;
    private final PdfService pdfService;
    private final RecipeRepository recipeRepository;


    @Override
    public ResponseEntity<RecipeDTO> getRecipeById(Long id) throws JsonProcessingException {
        log.info("Processing getting recipe from TalkyChef. Id of recipe: {}", id);
        Recipe recipe = recipeService.getRecipeById(id);
        RecipeDTO recipeDto = modelMapper.map(recipe, RecipeDTO.class);
        log.info("Got recipe with name [{}]", recipeDto.getRecipeName());
        return ResponseEntity.ok(recipeDto);
    }

    @Override
    public ResponseEntity<DailyMenuDTO> getDailyMenu(MenuInfoDTO menuInfoDTO) throws JsonProcessingException {
        log.info("Start forming daily menu");
        double bmr = dailyCalorieNeeds.calculateDailyCalorieNeeds(menuInfoDTO);

        DailyMenuDTO dailyMenuDTO = new DailyMenuDTO();
        double imt = menuInfoDTO.getWeight() / pow(menuInfoDTO.getHeight() / 100.0, 2);
        fillTotalParams(dailyMenuDTO, bmr, imt);

        List<ProductDTO> allowedProductsForSelfConsumption = productService.findAllowedProductsForSelfConsumptionForDiagnose(menuInfoDTO.getDiagnoseId());
        List<Long> notAllowedProductIds = recipeService.findNotAllowedProductsForDiagnose(menuInfoDTO.getDiagnoseId());

        createDailyMenu(bmr, dailyMenuDTO, notAllowedProductIds, allowedProductsForSelfConsumption);
        dailyCalorieNeeds.calculateTotalParameters(dailyMenuDTO);

        dailyMenuDTO.validateMenu();

        return ResponseEntity.ok(dailyMenuDTO);
    }


    @Override
    public ResponseEntity<MealTypeResponseDTO> changeMealType(String mealType, Long diagnoseId, DailyMenuDTO dailyMenuDTO) throws JsonProcessingException {
        List<ProductDTO> allowedProducts = productService.findAllowedProductsForSelfConsumptionForDiagnose(diagnoseId);
        TotalParamsDTO totalParams = dailyMenuDTO.getTotalParams();

        MealDTO changingMeal = changeService.getChangingMeal(mealType, dailyMenuDTO);
        changeService.updateTotalParams(totalParams, changingMeal, false);

        Meal meal = changeService.createAndFillMeal(mealType, allowedProducts, diagnoseId, totalParams, dailyMenuDTO.getAllRecipes());
        log.info("Meal created: {}", mealType);

        MealDTO mealDTO = modelMapper.map(meal, MealDTO.class);
        MealTypeResponseDTO mealTypeResponseDTO = new MealTypeResponseDTO();
        mealTypeResponseDTO.setMeal(mealDTO);

        changeService.updateTotalParams(totalParams, mealDTO, true);
        mealTypeResponseDTO.setTotalParams(totalParams);

        return ResponseEntity.ok(mealTypeResponseDTO);
    }


    public ResponseEntity<ProductChangeResponseDTO> changeProduct(Long productId, Long diagnoseId, DailyMenuDTO dailyMenuDTO) {
        log.info("Starting product change process for productId: {} and diagnoseId: {}", productId, diagnoseId);

        ProductDTO changingProduct;
        try {
            changingProduct = changeService.getChangingProduct(productId, dailyMenuDTO);
        } catch (NotFoundException e) {
            log.error("Product with ID {} not found.", productId, e);
            throw e;
        }

        List<ProductDTO> allowedProducts = productService.findAllowedProductsForSelfConsumptionForDiagnose(diagnoseId);

        TotalParamsDTO totalParams = dailyMenuDTO.getTotalParams();
        changeService.updateTotalParams(totalParams, changingProduct, false);

        List<ProductDTO> filteredProducts = changeService.filterAllowedItems(allowedProducts, dailyMenuDTO.getAllProducts(), ProductDTO::getProductId);

        Product product = changeService.createAndFillProduct(filteredProducts, totalParams);

        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        ProductChangeResponseDTO productChangeResponseDTO = changeService.createProductChangeResponse(productDTO, totalParams);

        log.info("Product change process completed successfully for productId: {}", productId);
        return ResponseEntity.ok(productChangeResponseDTO);
    }

    @Override
    public ResponseEntity<RecipeChangeResponseDTO> changeRecipe(Long recipeId, Long diagnoseId, DailyMenuDTO dailyMenuDTO) throws JsonProcessingException {
        log.info("Starting recipe change process for recipeId: {} and diagnoseId: {}", recipeId, diagnoseId);

        RecipeDTO changingRecipe = getChangingRecipe(recipeId, dailyMenuDTO);
        String daytime = determineDaytime(recipeId, dailyMenuDTO);
        List<Long> notAllowedProductsForDiagnose = recipeService.findNotAllowedProductsForDiagnose(diagnoseId);
        List<RecipeDTO> allowedRecipes = (recipeRepository.findRecipesNotContainingProductsForDaytime(notAllowedProductsForDiagnose, daytime)).stream().map(r -> modelMapper.map(r, RecipeDTO.class)).toList();

        TotalParamsDTO totalParams = dailyMenuDTO.getTotalParams();
        changeService.updateTotalParams(totalParams, changingRecipe, false);

        List<RecipeDTO> filteredRecipes = changeService.filterAllowedItems(allowedRecipes, dailyMenuDTO.getAllRecipes(), RecipeDTO::getRecipeId);
        RecipeDTO recipeDTO = changeService.getRecipeForParams(filteredRecipes, totalParams);
        RecipeChangeResponseDTO recipeChangeResponseDTO = changeService.createRecipeChangeResponse(recipeDTO, totalParams);

        log.info("Recipe change process completed successfully for recipeId: {}", recipeId);
        return ResponseEntity.ok(recipeChangeResponseDTO);
    }

    private RecipeDTO getChangingRecipe(Long recipeId, DailyMenuDTO dailyMenuDTO) {
        try {
            return changeService.getChangingRecipe(recipeId, dailyMenuDTO);
        } catch (NotFoundException e) {
            log.error("Recipe with ID {} not found.", recipeId, e);
            throw e;
        }
    }

    private String determineDaytime(Long recipeId, DailyMenuDTO dailyMenuDTO) {
        if (dailyMenuDTO.getBreakfastMeals().getRecipes().stream().anyMatch(r -> r.getRecipeId().equals(recipeId))) {
            return "Завтрак";
        } else if (dailyMenuDTO.getLunchMeals().getRecipes().stream().anyMatch(r -> r.getRecipeId().equals(recipeId))) {
            return "Обед";
        } else {
            return "Ужин";
        }
    }

    @Override
    public ResponseEntity<List<String>> getActivityLevels() {
        return ResponseEntity.ok(PhysicalActivityLevel.getAllDescriptions());
    }

    private List<RecipeDTO> fetchRecipes(List<RecipeDTO> recipes) {
        return recipes.stream()
                .map(rec -> modelMapper.map(recipeService.getRecipeById(rec.getRecipeId()), RecipeDTO.class)).toList();
    }

    @Override
    public ResponseEntity<byte[]> getPdf(DailyMenuDTO menu) {
        List<RecipeDTO> talkyBreakfast = fetchRecipes(menu.getBreakfastMeals().getRecipes());
        List<RecipeDTO> talkyLunch = fetchRecipes(menu.getLunchMeals().getRecipes());
        List<RecipeDTO> talkyDinner = fetchRecipes(menu.getDinnerMeals().getRecipes());

        String html = pdfService.generateHtml(menu, talkyBreakfast, talkyLunch, talkyDinner);
        return createResponseEntity(pdfService.generatePdfFromHtml(html));
    }


    private ResponseEntity<byte[]> createResponseEntity(byte[] pdfBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "daily_menu.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    private void createDailyMenu(double bmr, DailyMenuDTO dailyMenuDTO, List<Long> notAllowedProductIds, List<ProductDTO> allowedProducts) {
        final List<ProductDTO> mutableList = new ArrayList<>(allowedProducts);
        dailyMenuDTO.setBreakfastMeals(createMeal(MealType.BREAKFAST, notAllowedProductIds, mutableList, bmr, dailyMenuDTO.getAllRecipes()));
        dailyMenuDTO.setLunchMeals(createMeal(MealType.LUNCH, notAllowedProductIds, mutableList, bmr, dailyMenuDTO.getAllRecipes()));
        dailyMenuDTO.setDinnerMeals(createMeal(MealType.DINNER, notAllowedProductIds, mutableList, bmr, dailyMenuDTO.getAllRecipes()));
    }


    private MealDTO createMeal(MealType mealType, List<Long> notAllowedProductIds, List<ProductDTO> allowedProducts, double bmr, List<RecipeDTO> allRecipes) {
        log.info("Started creating meal: {}", mealType.getCategory());
        Meal meal = new Meal();
        meal.setMealRequirements(bmr, mealType.getPart());

        List<Recipe> recipeDTOS = recipeService.findRecipeWithinRangeAndCategory(notAllowedProductIds, meal, mealType.getCategory(), allRecipes);
        meal.setMealDetails(recipeDTOS);
        productService.fillMealWithProducts(meal, allowedProducts);
        log.info("Meal created: {}", mealType.getCategory());
        return modelMapper.map(meal, MealDTO.class);
    }


}
