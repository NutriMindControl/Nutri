package talky.dietcontrol.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static talky.dietcontrol.services.impl.DailyMenuServiceImpl.MINUS_FIVE_PERCENT;
import static talky.dietcontrol.services.impl.DailyMenuServiceImpl.PLUS_FIVE_PERCENT;

@Slf4j
@Data
public class DailyMenuDTO {
    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("breakfast_meals")
    private MealDTO breakfastMeals = new MealDTO();

    @JsonProperty("lunch_meals")
    private MealDTO lunchMeals = new MealDTO();

    @JsonProperty("dinner_meals")
    private MealDTO dinnerMeals = new MealDTO();

    @JsonProperty("total_params")
    private TotalParamsDTO totalParams;

    @JsonIgnore
    public List<ProductDTO> getAllProducts() {
        return Stream.of(breakfastMeals, lunchMeals, dinnerMeals)
                .flatMap(meal -> meal.getProducts().stream()).toList();
    }

    @JsonIgnore
    public List<RecipeDTO> getAllRecipes() {
        return Stream.of(breakfastMeals, lunchMeals, dinnerMeals)
                .flatMap(meal -> meal.getRecipes().stream()).toList();
    }

    public void validateMenu() {
        double maxCalories = totalParams.getRequiredCalories() * PLUS_FIVE_PERCENT;
        double minCalories = totalParams.getRequiredCalories() * MINUS_FIVE_PERCENT;
        double maxProteins = totalParams.getDailyProteinNeeds() * PLUS_FIVE_PERCENT;
        double minProteins = totalParams.getDailyProteinNeeds() * MINUS_FIVE_PERCENT;
        double maxFats = totalParams.getDailyFatNeeds() * PLUS_FIVE_PERCENT;
        double minFats = totalParams.getDailyFatNeeds() * MINUS_FIVE_PERCENT;
        double maxCarbohydrates = totalParams.getDailyCarbohydrateNeeds() * PLUS_FIVE_PERCENT;
        double minCarbohydrates = totalParams.getDailyCarbohydrateNeeds() * MINUS_FIVE_PERCENT;

        double totalCalories = totalParams.getTotalCalories();
        double totalProteins = totalParams.getTotalProteins();
        double totalFats = totalParams.getTotalFats();
        double totalCarbohydrates = totalParams.getTotalCarbohydrates();

        boolean withinRange = true;
        if (totalCalories > maxCalories || totalCalories < minCalories) {
            double deviation = ((totalCalories - totalParams.getRequiredCalories()) / totalParams.getRequiredCalories()) * 100;
            log.info("Total Calories are out of range. Deviation: {}%", String.format("%.2f", deviation));
            withinRange = false;
        }
        if (totalProteins > maxProteins || totalProteins < minProteins) {
            double deviation = ((totalProteins - totalParams.getDailyProteinNeeds()) / totalParams.getDailyProteinNeeds()) * 100;
            log.info("Total Proteins are out of range. Deviation: {}%", String.format("%.2f", deviation));
            withinRange = false;
        }
        if (totalFats > maxFats || totalFats < minFats) {
            double deviation = ((totalFats - totalParams.getDailyFatNeeds()) / totalParams.getDailyFatNeeds()) * 100;
            log.info("Total Fats are out of range. Deviation: {}%", String.format("%.2f", deviation));
            withinRange = false;
        }
        if (totalCarbohydrates > maxCarbohydrates || totalCarbohydrates < minCarbohydrates) {
            double deviation = ((totalCarbohydrates - totalParams.getDailyCarbohydrateNeeds()) / totalParams.getDailyCarbohydrateNeeds()) * 100;
            log.info("Total Carbohydrates are out of range. Deviation: {}%", String.format("%.2f", deviation));
            withinRange = false;
        }

        if (withinRange) {
            log.info("Daily menu successfully formed");
        } else {
            log.error("Daily menu is not balanced");
        }
    }

}