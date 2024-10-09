package talky.dietcontrol.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import talky.dietcontrol.exceptions.NotFoundException;
import talky.dietcontrol.model.dto.recipes.CategoryDto;
import talky.dietcontrol.model.dto.recipes.RecipeDTO;
import talky.dietcontrol.model.entities.*;
import talky.dietcontrol.repository.CategoryRepository;
import talky.dietcontrol.repository.ProductCategoryProhibitionRepository;
import talky.dietcontrol.repository.RecipeRepository;
import talky.dietcontrol.services.interfaces.DiagnosisService;
import talky.dietcontrol.services.interfaces.RecipeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
@AllArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService {
    private final RestTemplate restTemplate;
    private final DiagnosisService diagnosisService;
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final ModelMapper mapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductCategoryProhibitionRepository productRepository;
    private final Random random = new Random();


    private List<Recipe> filterRecipesByCategory(List<Long> diagnoseId, String dayTime) {
        log.info("Trying to find recipes for daytime {}", dayTime);

        List<Recipe> allowedRecipes = recipeRepository.findRecipesNotContainingProductsForDaytime(diagnoseId, dayTimeName(dayTime));

        if (allowedRecipes.isEmpty()) {
            log.info("Recipe with daytime {} not found", dayTime);
        }
        return allowedRecipes;
    }

    public String dayTimeName(String dayTime) {
        if (dayTime.equals("lunch")) dayTime = "Обед";
        else if (dayTime.equals("dinner")) dayTime = "Ужин";
        else dayTime = "Завтрак";
        return dayTime;
    }


    public List<Long> findNotAllowedProductsForDiagnose(Long diagnoseId) throws JsonProcessingException {
        log.info("Processing getting recipes from TalkyChef which have products allowed for diagnosis with id: {}", diagnoseId);

        Diagnosis diagnosis = diagnosisService.fetchDiagnosis(diagnoseId);

        List<ProductCategoryProhibition> notAllowedProducts = productRepository.findByCategoryNumber(diagnosis.getCode());
        return notAllowedProducts.stream().map(ProductCategoryProhibition::getProductId).toList();
    }

    public List<Recipe> findRecipeWithinRangeAndCategory(List<Long> diagnoseId, Meal meal, String category, List<RecipeDTO> allRecipes) {
        log.info("Started filling meal with dishes");
        List<Recipe> recipesDTO = filterRecipesByCategory(diagnoseId, category);

        Collections.shuffle(recipesDTO, random);
        List<Recipe> selectedRecipes = new ArrayList<>();
        int totalCalories = 0;
        int totalProtein = 0;
        int totalFat = 0;
        int totalCarbs = 0;
        for (Recipe recipe : recipesDTO) {
            if (allRecipes.stream().noneMatch(m -> m.getRecipeId().equals(recipe.getId()))) {
                if (totalCalories + recipe.getKilocalories() <= meal.getExpectedKilocalories()[1] && totalProtein + recipe.getProteins() <= meal.getExpectedProteins()[1] && totalFat + recipe.getFats() <= meal.getExpectedFats()[1] && totalCarbs + recipe.getCarbohydrates() <= meal.getExpectedCarbohydrates()[1]) {
                    selectedRecipes.add(recipe);
                    totalCalories += recipe.getKilocalories();
                    totalProtein += recipe.getProteins();
                    totalFat += recipe.getFats();
                    totalCarbs += recipe.getCarbohydrates();
                }
                if (selectedRecipes.size() == 2) break;
            }
        }
        log.info("Recipes with dishes were added: {}", selectedRecipes);

        return selectedRecipes;
    }

    @Override
    public List<CategoryDto> getCategoriesByRecipeId(Long id) {
        log.info("Processing get categories by recipe id [{}] request", id);
        List<Category> categories = categoryRepository.findByRecipeId(id);
        List<CategoryDto> categoryDtos = categories.stream().map(
                element -> mapper.map(element, CategoryDto.class)).toList();
        log.info("Response category list size: {}", categoryDtos.size());
        return categoryDtos;
    }

    @Override
    public List<Recipe> getRecipesByIds(List<Long> productIds, Integer limit) {
        List<Recipe> recipes = recipeRepository.findRecipesNotContainingProducts(productIds, limit);
        return recipes.stream().map(recipe -> mapper.map(recipe, Recipe.class)).toList();
    }

    @Override
    public Recipe getRecipeById(Long id) {
        log.info("Processing get recipe by id [{}] request", id);
        Recipe recipe = findRecipe(recipeRepository, id);
        Recipe recipeDto = mapper.map(recipe, Recipe.class);
        log.info("Processed get recipe by id request");
        return recipeDto;
    }

    public Recipe findRecipe(RecipeRepository repository, Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Couldn't find recipe with id: " + id));
    }

}
