package talky.dietcontrol.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import talky.dietcontrol.config.Constants;
import talky.dietcontrol.exceptions.NotFoundException;
import talky.dietcontrol.model.dto.*;
import talky.dietcontrol.model.entities.Product;
import talky.dietcontrol.services.interfaces.CalorieCalculatorService;
import talky.dietcontrol.services.interfaces.DailyMenuService;
import talky.dietcontrol.services.interfaces.ProductService;
import talky.dietcontrol.services.interfaces.RecipeService;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.pow;
import static talky.dietcontrol.services.impl.ChangeServiceImpl.fillTotalParams;

@Slf4j
@Service
public class DailyMenuServiceImpl implements DailyMenuService {
    public static final double CARBOHYDRATES_COEF = 0.4 / 4.1;
    public static final double FATS_COEF = 0.3 / 9.3;
    public static final double PROTEINS_COEF = 0.3 / 4.1;
    public static final double PLUS_FIVE_PERCENT = 1.05;
    public static final double MINUS_FIVE_PERCENT = 0.95;


    private final RestTemplate restTemplate;

    private final ModelMapper modelMapper;

    private final RecipeService recipeService;
    private final ProductService productService;
    private final CalorieCalculatorService dailyCalorieNeeds;
    private final ChangeServiceImpl changeService;
    private final PdfService pdfService;

    public DailyMenuServiceImpl(RestTemplate restTemplate, ModelMapper modelMapper, RecipeService recipeService, ProductService productService, CalorieCalculatorService dailyCalorieNeeds, ChangeServiceImpl changeService, PdfService pdfService) {
        this.restTemplate = restTemplate;
        this.modelMapper = modelMapper;
        this.recipeService = recipeService;
        this.productService = productService;
        this.dailyCalorieNeeds = dailyCalorieNeeds;
        this.changeService = changeService;
        this.pdfService = pdfService;
    }


    @Override
    public ResponseEntity<TalkyRecipeDTO> getRecipeById(Long id) throws JsonProcessingException {
        log.info("Processing getting recipe from TalkyChef. Id of recipe: {}", id);
        ResponseEntity<String> response = restTemplate.getForEntity(URI.create(Constants.TALKY_URL + "/recipes/" + id), String.class);
        if (response.getBody() == null) {
            log.error("Couldn't get recipe, check id");
            throw new NotFoundException("Couldn't get recipe");
        }
        TalkyRecipeDTO recipeDTO = new ObjectMapper().readValue(response.getBody(), TalkyRecipeDTO.class);
        log.info("Got recipe with name [{}]", recipeDTO.getName());
        return ResponseEntity.ok(recipeDTO);
    }

    @Override
    public ResponseEntity<DailyMenuDTO> getDailyMenu(MenuInfoDTO menuInfoDTO) throws JsonProcessingException {
        log.info("Start forming daily menu");
        double bmr = dailyCalorieNeeds.calculateDailyCalorieNeeds(menuInfoDTO);

        DailyMenuDTO dailyMenuDTO = new DailyMenuDTO();
        double imt = menuInfoDTO.getWeight() / pow(menuInfoDTO.getHeight() / 100.0, 2);
        fillTotalParams(dailyMenuDTO, bmr, imt);

        List<ProductDTO> allowedProductsForSelfConsumption = productService.findAllowedProductsForSelfConsumptionForDiagnose(menuInfoDTO.getDiagnoseId());
        List<RecipeDTO> allowedRecipes = recipeService.findAllowedRecipesForDiagnose(menuInfoDTO.getDiagnoseId());

        createDailyMenu(bmr, dailyMenuDTO, allowedRecipes, allowedProductsForSelfConsumption);
        dailyCalorieNeeds.calculateTotalParameters(dailyMenuDTO);

        dailyMenuDTO.validateMenu();

        return ResponseEntity.ok(dailyMenuDTO);
    }


    @Override
    public ResponseEntity<MealTypeResponseDTO> changeMealType(String mealType, Long diagnoseId, DailyMenuDTO dailyMenuDTO) throws JsonProcessingException {
        List<ProductDTO> allowedProducts = productService.findAllowedProductsForSelfConsumptionForDiagnose(diagnoseId);
        List<RecipeDTO> allowedRecipes = recipeService.findAllowedRecipesForDiagnose(diagnoseId);
        TotalParamsDTO totalParams = dailyMenuDTO.getTotalParams();

        MealDTO changingMeal = changeService.getChangingMeal(mealType, dailyMenuDTO);
        changeService.updateTotalParams(totalParams, changingMeal, false);

        Meal meal = changeService.createAndFillMeal(mealType, allowedProducts, allowedRecipes, totalParams);
        log.info("Meal created: {}", mealType);

        MealDTO mealDTO = modelMapper.map(meal, talky.dietcontrol.model.dto.MealDTO.class);
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

        RecipeDTO changingRecipe;
        try {
            changingRecipe = changeService.getChangingRecipe(recipeId, dailyMenuDTO);
        } catch (NotFoundException e) {
            log.error("Recipe with ID {} not found.", recipeId, e);
            throw e;
        }

        List<RecipeDTO> allowedRecipes = recipeService.findAllowedRecipesForDiagnose(diagnoseId);

        TotalParamsDTO totalParams = dailyMenuDTO.getTotalParams();
        changeService.updateTotalParams(totalParams, changingRecipe, false);

        List<RecipeDTO> filteredRecipes = changeService.filterAllowedItems(allowedRecipes, dailyMenuDTO.getAllRecipes(), RecipeDTO::getRecipeId);

        RecipeDTO recipeDTO = changeService.getRecipeForParams(filteredRecipes, totalParams);

        RecipeChangeResponseDTO recipeChangeResponseDTO = changeService.createRecipeChangeResponse(recipeDTO, totalParams);

        log.info("Recipe change process completed successfully for recipeId: {}", recipeId);
        return ResponseEntity.ok(recipeChangeResponseDTO);
    }

    @Override
    public ResponseEntity<List<String>> getActivityLevels() {
        return ResponseEntity.ok(PhysicalActivityLevel.getAllDescriptions());
    }

    private List<TalkyRecipeDTO> fetchRecipes(List<RecipeDTO> recipes) {
        return recipes.stream()
                .map(rec -> {
                    try {
                        ResponseEntity<TalkyRecipeDTO> responseEntity = getRecipeById(rec.getRecipeId());
                        return Optional.ofNullable(responseEntity.getBody())
                                .orElseThrow(() -> new RuntimeException("Recipe not found for ID: " + rec.getRecipeId()));
                    } catch (JsonProcessingException e) {
                        throw new NotFoundException("Error processing recipe with ID: " + rec.getRecipeId());
                    }
                }).toList();
    }

    @Override
    public ResponseEntity<byte[]> getPdf(DailyMenuDTO menu) {
        List<TalkyRecipeDTO> talkyBreakfast = fetchRecipes(menu.getBreakfastMeals().getRecipes());
        List<TalkyRecipeDTO> talkyLunch = fetchRecipes(menu.getLunchMeals().getRecipes());
        List<TalkyRecipeDTO> talkyDinner = fetchRecipes(menu.getDinnerMeals().getRecipes());

        String html = pdfService.generateHtml(menu, talkyBreakfast, talkyLunch, talkyDinner);
        return createResponseEntity(pdfService.generatePdfFromHtml(html));
    }


    private ResponseEntity<byte[]> createResponseEntity(byte[] pdfBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "daily_menu.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    private void createDailyMenu(double bmr, DailyMenuDTO dailyMenuDTO, List<RecipeDTO> allowedRecipes, List<ProductDTO> allowedProducts) {
        final List<ProductDTO> mutableList = new ArrayList<>(allowedProducts);
        final List<RecipeDTO> mutableListRecipes = new ArrayList<>(allowedRecipes);
        Collections.shuffle(mutableListRecipes);
        dailyMenuDTO.setBreakfastMeals(createMeal(MealType.BREAKFAST, mutableListRecipes, mutableList, bmr));
        productService.removeUsedProducts(mutableList, mutableListRecipes, dailyMenuDTO.getBreakfastMeals());
        dailyMenuDTO.setLunchMeals(createMeal(MealType.LUNCH, mutableListRecipes, mutableList, bmr));
        productService.removeUsedProducts(mutableList, mutableListRecipes, dailyMenuDTO.getLunchMeals());
        dailyMenuDTO.setDinnerMeals(createMeal(MealType.DINNER, mutableListRecipes, mutableList, bmr));
    }


    private MealDTO createMeal(MealType mealType, List<RecipeDTO> allowedRecipes, List<ProductDTO> allowedProducts, double bmr) {
        log.info("Started creating meal: {}", mealType.getCategory());
        Meal meal = new Meal();
        meal.setMealRequirements(bmr, mealType.getPart());

        List<RecipeDTO> recipeDTOS = recipeService.findRecipeWithinRangeAndCategory(allowedRecipes, meal, mealType.getCategory());
        meal.setMealDetails(recipeDTOS);
        productService.fillMealWithProducts(meal, allowedProducts);
        log.info("Meal created: {}", mealType.getCategory());
        return modelMapper.map(meal, MealDTO.class);
    }


}
