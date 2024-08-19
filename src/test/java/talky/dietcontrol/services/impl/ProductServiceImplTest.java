package talky.dietcontrol.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import talky.dietcontrol.model.dto.Meal;
import talky.dietcontrol.model.dto.MealDTO;
import talky.dietcontrol.model.dto.ProductDTO;
import talky.dietcontrol.model.dto.RecipeDTO;
import talky.dietcontrol.model.entities.Product;
import talky.dietcontrol.repository.ProductsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllowedProductsForSelfConsumptionForDiagnose() {
        Long diagnoseId = 1L;
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        products.add(product);

        when(productsRepository.findAllowedProducts(diagnoseId)).thenReturn(products);
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(new ProductDTO());

        List<ProductDTO> result = productServiceImpl.findAllowedProductsForSelfConsumptionForDiagnose(diagnoseId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productsRepository, times(1)).findAllowedProducts(diagnoseId);
        verify(modelMapper, times(1)).map(any(Product.class), eq(ProductDTO.class));
    }

    @Test
    void testFillMealWithProducts() {
        Meal meal = new Meal();
        meal.setMealDetails(Collections.emptyList());
        meal.setExpectedKilocalories(new Double[]{1000.0, 1200.0});
        meal.setExpectedProteins(new Double[]{50.0, 70.0});
        meal.setExpectedFats(new Double[]{30.0, 50.0});
        meal.setExpectedCarbohydrates(new Double[]{100.0, 150.0});

        List<ProductDTO> products = new ArrayList<>();
        ProductDTO productDTO = new ProductDTO(1L, "Product 1", 10.0, 7.0, 21.0, 148.0, 100.0, null);
        products.add(productDTO);

        productServiceImpl.fillMealWithProducts(meal, products);

        assertFalse(meal.getProducts().isEmpty());
        assertEquals(1, meal.getProducts().size());
        verify(productsRepository, never()).findAllowedProducts(anyLong());
    }

    @Test
    void testRemoveUsedProducts() {
        List<ProductDTO> products = new ArrayList<>();
        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setProductId(1L);
        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setProductId(2L);
        products.add(productDTO1);
        products.add(productDTO2);

        List<RecipeDTO> recipes = new ArrayList<>();
        RecipeDTO recipeDTO1 = new RecipeDTO();
        recipeDTO1.setRecipeId(1L);
        RecipeDTO recipeDTO2 = new RecipeDTO();
        recipeDTO2.setRecipeId(2L);
        recipes.add(recipeDTO1);
        recipes.add(recipeDTO2);

        MealDTO meal = new MealDTO();
        meal.setProducts(List.of(productDTO1));
        meal.setRecipes(List.of(recipeDTO1));

        productServiceImpl.removeUsedProducts(products, recipes, meal);

        assertEquals(1, products.size());
        assertEquals(1, recipes.size());
        assertEquals(2L, products.get(0).getProductId());
        assertEquals(2L, recipes.get(0).getRecipeId());
    }
}
