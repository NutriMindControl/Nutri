package talky.dietcontrol.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import talky.dietcontrol.model.dto.*;
import talky.dietcontrol.model.entities.Product;
import talky.dietcontrol.services.interfaces.ProductService;
import talky.dietcontrol.services.interfaces.RecipeService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeServiceImplTest {

    @Mock
    private RecipeService recipeService;

    @Mock
    private ProductService productService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChangeServiceImpl changeService;

    private DailyMenuDTO dailyMenuDTO;
    private TotalParamsDTO totalParamsDTO;
    private ProductDTO productDTO;
    private RecipeDTO recipeDTO;

    @BeforeEach
    public void setUp() {
        dailyMenuDTO = new DailyMenuDTO();
        totalParamsDTO = new TotalParamsDTO(1200.0, 40.0, 60.0, 140.0, 2500.0, 70.0, 70.0, 300.0, 22.0);
        dailyMenuDTO.setTotalParams(totalParamsDTO);
        productDTO = new ProductDTO(1L, "Product1", 50.0, 10.0, 5.0, 20.0, 100.0, "Test1");
        recipeDTO = new RecipeDTO("Spaghetti Bolognese", 1L, 30, 15, 4, 600.0, 25.0, 20.0, 70.0, List.of(new IngredientsDistributionDto()));
    }

    @Test
    void testGetChangingMeal() {
        dailyMenuDTO.setBreakfastMeals(new MealDTO());
        dailyMenuDTO.setLunchMeals(new MealDTO());
        dailyMenuDTO.setDinnerMeals(new MealDTO());

        assertEquals(dailyMenuDTO.getBreakfastMeals(), changeService.getChangingMeal("breakfast", dailyMenuDTO));
        assertEquals(dailyMenuDTO.getLunchMeals(), changeService.getChangingMeal("lunch", dailyMenuDTO));
        assertEquals(dailyMenuDTO.getDinnerMeals(), changeService.getChangingMeal("dinner", dailyMenuDTO));
    }

    @Test
    void testCreateAndFillMeal() {
        when(recipeService.findRecipeWithinRangeAndCategory(anyList(), any(Meal.class), anyString()))
                .thenReturn(Collections.singletonList(recipeDTO));

        Meal meal = changeService.createAndFillMeal("breakfast", Collections.singletonList(productDTO),
                Collections.singletonList(recipeDTO), totalParamsDTO);

        assertNotNull(meal);
        assertEquals(1, meal.getRecipes().size());
        verify(productService, times(1)).fillMealWithProducts(eq(meal), anyList());
    }

    @Test
    void testUpdateTotalParams() {
        changeService.updateTotalParams(totalParamsDTO, productDTO, true);
        // Добавьте проверки на корректное обновление параметров
    }

    @Test
    void testGetRecipeForParams() {
        List<RecipeDTO> filteredRecipes = Collections.singletonList(recipeDTO);
        RecipeDTO result = changeService.getRecipeForParams(filteredRecipes, totalParamsDTO);

        assertNotNull(result);
    }

    @Test
    void testCreateProductChangeResponse() {
        ProductChangeResponseDTO response = changeService.createProductChangeResponse(productDTO, totalParamsDTO);

        assertNotNull(response);
        assertEquals(productDTO, response.getProduct());
        assertEquals(totalParamsDTO, response.getTotalParams());
    }

    @Test
    void testCreateRecipeChangeResponse() {
        RecipeChangeResponseDTO response = changeService.createRecipeChangeResponse(recipeDTO, totalParamsDTO);

        assertNotNull(response);
        assertEquals(recipeDTO, response.getRecipe());
        assertEquals(totalParamsDTO, response.getTotalParams());
    }

    @Test
    void testGetChangingProduct() {
        dailyMenuDTO.getBreakfastMeals().setProducts(Collections.singletonList(productDTO));
        productDTO.setProductId(1L);

        ProductDTO result = changeService.getChangingProduct(1L, dailyMenuDTO);
        assertNotNull(result);
        assertEquals(productDTO, result);
    }

    @Test
    void testGetChangingRecipe() {
        dailyMenuDTO.getBreakfastMeals().setRecipes(Collections.singletonList(recipeDTO));
        recipeDTO.setRecipeId(1L);

        RecipeDTO result = changeService.getChangingRecipe(1L, dailyMenuDTO);
        assertNotNull(result);
        assertEquals(recipeDTO, result);
    }


    @Test
    void testFillTotalParams() {
        DailyMenuDTO dailyMenuDTO = new DailyMenuDTO();
        ChangeServiceImpl.fillTotalParams(dailyMenuDTO, 2000, 25);
        assertNotNull(dailyMenuDTO.getTotalParams());
    }

    @Test
    void testCreateAndFillProduct() {
        when(modelMapper.map(any(ProductDTO.class), eq(Product.class)))
                .thenReturn(new Product());

        Product product = changeService.createAndFillProduct(Collections.singletonList(productDTO), totalParamsDTO);

        assertNotNull(product);
    }

    @Test
    void testCalculateDifference() {
        double difference = changeService.calculateDifference(productDTO, totalParamsDTO);
        assertTrue(difference >= 0);
    }
}
