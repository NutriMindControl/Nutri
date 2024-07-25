package talky.dietcontrol.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import talky.dietcontrol.model.dto.DailyMenuDTO;
import talky.dietcontrol.model.dto.MealDTO;
import talky.dietcontrol.model.dto.MenuInfoDTO;
import talky.dietcontrol.model.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalorieCalculatorServiceImplTest {

    @InjectMocks
    private CalorieCalculatorServiceImpl calorieCalculatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateDailyCalorieNeeds_Male() {
        MenuInfoDTO menuInfoDTO = new MenuInfoDTO();
        menuInfoDTO.setWeight(70.0);
        menuInfoDTO.setHeight(175.0);
        menuInfoDTO.setAge(30L);
        menuInfoDTO.setGender("Жен.");
        menuInfoDTO.setPhysicalActivityLevel("Отсутствие физической активности");

        double dailyCalorieNeeds = calorieCalculatorService.calculateDailyCalorieNeeds(menuInfoDTO);

        assertEquals(1483, dailyCalorieNeeds);
    }

    @Test
    void testCalculateDailyCalorieNeeds_Female() {
        MenuInfoDTO menuInfoDTO = new MenuInfoDTO();
        menuInfoDTO.setWeight(60.0);
        menuInfoDTO.setHeight(165.0);
        menuInfoDTO.setAge(25L);
        menuInfoDTO.setGender("Жен.");
        menuInfoDTO.setPhysicalActivityLevel("Отсутствие физической активности");

        double dailyCalorieNeeds = calorieCalculatorService.calculateDailyCalorieNeeds(menuInfoDTO);

        assertEquals(1345, dailyCalorieNeeds);
    }

    @Test
    void testSolveNutrientBalance() {
        List<ProductDTO> fixedProducts = new ArrayList<>();
        List<ProductDTO> products = new ArrayList<>();
        products.add(new ProductDTO(1L, "Product1", 50.0, 10.0, 5.0, 20.0, 100.0, "Test1"));
        products.add(new ProductDTO(2L, "Product2", 100.0, 20.0, 10.0, 40.0, 100.0, "Test1"));
        products.add(new ProductDTO(3L, "Product3", 200.0, 30.0, 20.0, 60.0, 100.0, "Test1"));

        boolean result = CalorieCalculatorServiceImpl.solveNutrientBalance(fixedProducts, products, 500, 50, 30, 100);

        assertTrue(result);
    }

    @Test
    void testCalculateTotalParameters() {
        MealDTO breakfast = new MealDTO(10.0, 20.0, 5.0, 200.0);
        MealDTO lunch = new MealDTO(20.0, 60.0, 20.0, 600.0);
        MealDTO dinner = new MealDTO(20.0, 50.0, 15.0, 400.0);

        DailyMenuDTO dailyMenuDTO = new DailyMenuDTO();
        dailyMenuDTO.setBreakfastMeals(breakfast);
        dailyMenuDTO.setLunchMeals(lunch);
        dailyMenuDTO.setDinnerMeals(dinner);

        calorieCalculatorService.calculateTotalParameters(dailyMenuDTO);

        assertEquals(1200, dailyMenuDTO.getTotalParams().getTotalCalories());
        assertEquals(40, dailyMenuDTO.getTotalParams().getTotalProteins());
        assertEquals(50, dailyMenuDTO.getTotalParams().getTotalFats());
        assertEquals(130, dailyMenuDTO.getTotalParams().getTotalCarbohydrates());
    }
}