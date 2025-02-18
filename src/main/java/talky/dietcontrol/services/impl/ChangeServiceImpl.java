package talky.dietcontrol.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import talky.dietcontrol.exceptions.NotFoundException;
import talky.dietcontrol.model.dto.dailymenu.BaseDTOWithNutrientsMethods;
import talky.dietcontrol.model.dto.dailymenu.DailyMenuDTO;
import talky.dietcontrol.model.dto.dailymenu.MealDTO;
import talky.dietcontrol.model.dto.dailymenu.TotalParamsDTO;
import talky.dietcontrol.model.dto.products.ProductChangeResponseDTO;
import talky.dietcontrol.model.dto.products.ProductDTO;
import talky.dietcontrol.model.dto.recipes.RecipeChangeResponseDTO;
import talky.dietcontrol.model.dto.recipes.RecipeDTO;
import talky.dietcontrol.model.entities.Meal;
import talky.dietcontrol.model.entities.Product;
import talky.dietcontrol.model.entities.Recipe;
import talky.dietcontrol.services.interfaces.ChangeService;
import talky.dietcontrol.services.interfaces.ProductService;
import talky.dietcontrol.services.interfaces.RecipeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static talky.dietcontrol.services.impl.DailyMenuServiceImpl.MINUS_FIVE_PERCENT;
import static talky.dietcontrol.services.impl.DailyMenuServiceImpl.PLUS_FIVE_PERCENT;

@Service
public class ChangeServiceImpl implements ChangeService {

    private final RecipeService recipeService;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    @Autowired
    public ChangeServiceImpl(RecipeService recipeService, ProductService productService, ModelMapper modelMapper) {
        this.recipeService = recipeService;
        this.productService = productService;
        this.modelMapper = modelMapper;
    }


    public MealDTO getChangingMeal(String mealType, DailyMenuDTO dailyMenuDTO) {
        return switch (mealType) {
            case "breakfast" -> dailyMenuDTO.getBreakfastMeals();
            case "lunch" -> dailyMenuDTO.getLunchMeals();
            default -> dailyMenuDTO.getDinnerMeals();
        };
    }

    public Meal createAndFillMeal(String mealType, List<ProductDTO> allowedProducts, Long diagnoseId, TotalParamsDTO totalParams, List<RecipeDTO> allRecipes) throws JsonProcessingException {
        Meal meal = getMealRequiredParams(totalParams);
        List<Long> notAllowedProductIds = recipeService.findNotAllowedProductsForDiagnose(diagnoseId);
        List<Recipe> recipeDTOS = recipeService.findRecipeWithinRangeAndCategory(notAllowedProductIds, meal, mealType, allRecipes);
        meal.setMealDetails(recipeDTOS);
        productService.fillMealWithProducts(meal, allowedProducts);
        meal.sumNutrients();
        return meal;
    }

    private static Meal getMealRequiredParams(TotalParamsDTO totalParams) {
        Meal meal = new Meal();
        meal.setExpectedKilocalories(new Double[]{totalParams.getRequiredCalories() * MINUS_FIVE_PERCENT - totalParams.getTotalCalories(), totalParams.getRequiredCalories() * PLUS_FIVE_PERCENT - totalParams.getTotalCalories()});
        meal.setExpectedFats(new Double[]{totalParams.getDailyFatNeeds() * MINUS_FIVE_PERCENT - totalParams.getTotalFats(), totalParams.getDailyFatNeeds() * PLUS_FIVE_PERCENT - totalParams.getTotalFats()});
        meal.setExpectedCarbohydrates(new Double[]{totalParams.getDailyCarbohydrateNeeds() * MINUS_FIVE_PERCENT - totalParams.getTotalCarbohydrates(), totalParams.getDailyCarbohydrateNeeds() * PLUS_FIVE_PERCENT - totalParams.getTotalCarbohydrates()});
        meal.setExpectedProteins(new Double[]{totalParams.getDailyProteinNeeds() * MINUS_FIVE_PERCENT - totalParams.getTotalProteins(), totalParams.getDailyProteinNeeds() * PLUS_FIVE_PERCENT - totalParams.getTotalProteins()});
        return meal;
    }

    public <T extends BaseDTOWithNutrientsMethods> void updateTotalParams(TotalParamsDTO totalParams, T mealDTO, boolean isAdding) {
        int factor = isAdding ? 1 : -1;
        totalParams.setTotalCalories(totalParams.getTotalCalories() + factor * mealDTO.getCalories());
        totalParams.setTotalCarbohydrates(totalParams.getTotalCarbohydrates() + factor * mealDTO.getCarbohydrates());
        totalParams.setTotalFats(totalParams.getTotalFats() + factor * mealDTO.getFats());
        totalParams.setTotalProteins(totalParams.getTotalProteins() + factor * mealDTO.getProteins());
    }

    public RecipeDTO getRecipeForParams(List<RecipeDTO> filteredRecipes, TotalParamsDTO totalParams) {
        RecipeDTO bestProduct = null;
        double minDifference = Double.MAX_VALUE;

        for (RecipeDTO allowedRecipe : filteredRecipes) {
            double difference = calculateDifference(allowedRecipe, totalParams);

            if (difference < minDifference) {
                minDifference = difference;
                bestProduct = allowedRecipe;
            }
        }

        return bestProduct;
    }

    public ProductChangeResponseDTO createProductChangeResponse(ProductDTO productDTO, TotalParamsDTO totalParams) {
        ProductChangeResponseDTO response = new ProductChangeResponseDTO();
        response.setProduct(productDTO);
        updateTotalParams(totalParams, productDTO, true);
        response.setTotalParams(totalParams);
        return response;
    }

    public RecipeChangeResponseDTO createRecipeChangeResponse(RecipeDTO recipeDTO, TotalParamsDTO totalParams) {
        RecipeChangeResponseDTO response = new RecipeChangeResponseDTO();
        response.setRecipe(recipeDTO);
        updateTotalParams(totalParams, recipeDTO, true);
        response.setTotalParams(totalParams);
        return response;
    }


    public ProductDTO getChangingProduct(Long productId, DailyMenuDTO dailyMenuDTO) {
        return dailyMenuDTO.getAllProducts().stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Such product wasn't found"));
    }

    public Product createAndFillProduct(List<ProductDTO> allowedProducts, TotalParamsDTO totalParams) {

        Product bestProduct = null;
        double minDifference = Double.MAX_VALUE;

        for (ProductDTO allowedProduct : allowedProducts) {
            double difference = calculateDifference(allowedProduct, totalParams);

            if (difference < minDifference) {
                minDifference = difference;
                bestProduct = modelMapper.map(allowedProduct, Product.class);
            }
        }

        return bestProduct;
    }

    protected <T extends BaseDTOWithNutrientsMethods> double calculateDifference(T product, TotalParamsDTO totalParams) {
        double difference = 0;
        difference += Math.abs(product.getCalories() - (totalParams.getRequiredCalories() - totalParams.getTotalCalories()));
        difference += Math.abs(product.getProteins() - (totalParams.getDailyProteinNeeds() - totalParams.getTotalProteins()));
        difference += Math.abs(product.getFats() - (totalParams.getDailyFatNeeds() - totalParams.getTotalFats()));
        difference += Math.abs(product.getCarbohydrates() - (totalParams.getDailyCarbohydrateNeeds() - totalParams.getTotalCarbohydrates()));

        return difference;
    }

    public RecipeDTO getChangingRecipe(Long recipeId, DailyMenuDTO dailyMenuDTO) {
        return dailyMenuDTO.getAllRecipes().stream()
                .filter(product -> product.getRecipeId().equals(recipeId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Such recipe wasn't found"));
    }


    public <T, ID> List<T> filterAllowedItems(List<T> allowedItems, List<T> allItemsInMenu, Function<T, ID> idExtractor) {
        Set<ID> allItemIds = allItemsInMenu.stream()
                .map(idExtractor)
                .collect(Collectors.toSet());

        return allowedItems.stream()
                .filter(item -> !allItemIds.contains(idExtractor.apply(item)))
                .toList();
    }


    public static void fillTotalParams(DailyMenuDTO dailyMenuDTO, double bmr, double imt) {
        double dailyProteinNeeds = bmr * 0.3 / 4.1;
        double dailyFatNeeds = bmr * 0.3 / 9.3;
        double dailyCarbohydrateNeeds = bmr * 0.4 / 4.1;
        TotalParamsDTO totalParamsDTO = new TotalParamsDTO(bmr, dailyProteinNeeds, dailyFatNeeds, dailyCarbohydrateNeeds, imt);
        dailyMenuDTO.setDate(LocalDate.now());
        dailyMenuDTO.setTotalParams(totalParamsDTO);
    }


}