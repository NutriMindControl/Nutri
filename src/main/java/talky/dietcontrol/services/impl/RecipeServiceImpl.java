package talky.dietcontrol.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import talky.dietcontrol.config.Constants;
import talky.dietcontrol.exceptions.NotFoundException;
import talky.dietcontrol.model.dto.CategoryDto;
import talky.dietcontrol.model.dto.Meal;
import talky.dietcontrol.model.dto.RecipeDTO;
import talky.dietcontrol.model.entities.Diagnosis;
import talky.dietcontrol.model.entities.ProductCategoryProhibition;
import talky.dietcontrol.repository.ProductCategoryProhibitionRepository;
import talky.dietcontrol.services.interfaces.DiagnosisService;
import talky.dietcontrol.services.interfaces.RecipeService;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
    private final RestTemplate restTemplate;
    private final DiagnosisService diagnosisService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductCategoryProhibitionRepository productRepository;
    private final Random random = new Random();

    public RecipeServiceImpl(RestTemplate restTemplate, DiagnosisService diagnosisService, ProductCategoryProhibitionRepository productRepository) {
        this.restTemplate = restTemplate;
        this.diagnosisService = diagnosisService;
        this.productRepository = productRepository;
    }

    private List<RecipeDTO> filterRecipesByCategory
            (List<RecipeDTO> recipes, String dayTime) {
        log.info("Trying to find recipes for daytime {}", dayTime);

        List<RecipeDTO> allowedRecipes = new ArrayList<>();
        for (RecipeDTO recipe : recipes) {

            ResponseEntity<CategoryDto[]> responseEntity = restTemplate.getForEntity(URI.create(Constants.TALKY_URL + "/recipes/" + recipe.getId() + "/categories"), CategoryDto[].class);

            List<CategoryDto> categories = Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
            boolean hasDayTimeCategory = categories.stream().anyMatch(categoryDto -> categoryDto.getName().equals(dayTime));
            if (hasDayTimeCategory) {
                allowedRecipes.add(recipe);
            }
        }
        if (allowedRecipes.isEmpty()) {
            log.info("Recipe with daytime {} not found", dayTime);
        }
        return allowedRecipes;
    }


    public List<RecipeDTO> findAllowedRecipesForDiagnose(Long diagnoseId) throws JsonProcessingException {
        log.info("Processing getting recipes from TalkyChef which have products allowed for diagnosis with id: {}", diagnoseId);

        Diagnosis diagnosis = diagnosisService.fetchDiagnosis(diagnoseId);

        List<ProductCategoryProhibition> notAllowedProducts = productRepository.findByCategoryNumber(diagnosis.getCode());
        List<Long> notAllowedProductsIds = notAllowedProducts.stream().map(ProductCategoryProhibition::getProductId).toList();
        String notAllowedProductsIdsList = objectMapper.writeValueAsString(notAllowedProductsIds);

        String url = Constants.TALKY_URL + "/recipes/find-by-ids";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(notAllowedProductsIdsList, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new NotFoundException("Failed to fetch recipes. Status code: " + responseEntity.getStatusCode());
        }
        if (responseEntity.getBody() == null) {
            log.error("Couldn't get recipes, check diagnoseId");
            throw new NotFoundException("Couldn't get recipes");
        }

        List<RecipeDTO> recipes = objectMapper.readValue(responseEntity.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, RecipeDTO.class));

        log.info("Got recipes for diagnose with id: {}", diagnoseId);

        return recipes;
    }

    public List<RecipeDTO> findRecipeWithinRangeAndCategory(List<RecipeDTO> recipes, Meal meal, String category) {
        log.info("Started filling meal with dishes");
        List<RecipeDTO> recipesDTO = filterRecipesByCategory(recipes, category);

        Collections.shuffle(recipesDTO, random);
        List<RecipeDTO> selectedRecipes = new ArrayList<>();
        int totalCalories = 0;
        int totalProtein = 0;
        int totalFat = 0;
        int totalCarbs = 0;
        for (RecipeDTO recipe : recipesDTO) {
            if (totalCalories + recipe.getKilocalories() <= meal.getExpectedKilocalories()[1] &&
                    totalProtein + recipe.getProteins() <= meal.getExpectedProteins()[1] &&
                    totalFat + recipe.getFats() <= meal.getExpectedFats()[1] &&
                    totalCarbs + recipe.getCarbohydrates() <= meal.getExpectedCarbohydrates()[1]) {
                selectedRecipes.add(recipe);
                totalCalories += recipe.getKilocalories();
                totalProtein += recipe.getProteins();
                totalFat += recipe.getFats();
                totalCarbs += recipe.getCarbohydrates();
            }
        }
        log.info("Recipes with dishes were added: {}", selectedRecipes);

        return selectedRecipes;
    }


}
