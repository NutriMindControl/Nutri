package talky.dietcontrol.model.entities;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import talky.dietcontrol.model.dto.products.ProductDTO;

import java.util.ArrayList;
import java.util.List;

import static talky.dietcontrol.services.impl.DailyMenuServiceImpl.*;

@Data
@Slf4j
public class Meal {
    List<Recipe> recipes = new ArrayList<>();
    List<ProductDTO> products = new ArrayList<>();
    Double[] expectedKilocalories;
    Double[] expectedFats;
    Double[] expectedCarbohydrates;
    Double[] expectedProteins;

    Double calories;
    Double fats;
    Double carbohydrates;
    Double proteins;

    public void sumNutrients() {
        if (products != null && !products.isEmpty()) {
            calories = products.stream().mapToDouble(ProductDTO::getCalories).sum();
            fats = products.stream().mapToDouble(ProductDTO::getFats).sum();
            carbohydrates = products.stream().mapToDouble(ProductDTO::getCarbohydrates).sum();
            proteins = products.stream().mapToDouble(ProductDTO::getProteins).sum();
        } else {
            calories = 0.0;
            fats = 0.0;
            carbohydrates = 0.0;
            proteins = 0.0;
        }

        if (recipes != null && !recipes.isEmpty()) {
            calories += recipes.stream().mapToDouble(Recipe::getKilocalories).sum();
            fats += recipes.stream().mapToDouble(Recipe::getFats).sum();
            carbohydrates += recipes.stream().mapToDouble(Recipe::getCarbohydrates).sum();
            proteins += recipes.stream().mapToDouble(Recipe::getProteins).sum();
        }
    }

    public boolean validateMeal() {
        boolean withinRange = true;
        double maxAllowedExcess = PLUS_FIVE_PERCENT;
        double minAllowedExcess = MINUS_FIVE_PERCENT;


        if (calories < expectedKilocalories[0] * minAllowedExcess || calories > expectedKilocalories[1] * maxAllowedExcess) {
            withinRange = false;
            log.debug("Kilocalories are out of range.");
        }
        if (fats < expectedFats[0] || fats > expectedFats[1]) {
            withinRange = false;
            log.debug("Fats are out of range.");
        }
        if (carbohydrates < expectedCarbohydrates[0] * minAllowedExcess || carbohydrates > expectedCarbohydrates[1] * maxAllowedExcess) {
            withinRange = false;
            log.debug("Carbohydrates are out of range.");
        }
        if (proteins < expectedProteins[0] * minAllowedExcess || proteins > expectedProteins[1] * maxAllowedExcess) {
            withinRange = false;
            log.debug("Proteins are out of range.");
        }

        double kilocaloriesDeviation = Math.max(calories - expectedKilocalories[1], expectedKilocalories[0] - calories);
        double fatsDeviation = Math.max(fats - expectedFats[1], expectedFats[0] - fats);
        double carbohydratesDeviation = Math.max(carbohydrates - expectedCarbohydrates[1], expectedCarbohydrates[0] - carbohydrates);
        double proteinsDeviation = Math.max(proteins - expectedProteins[1], expectedProteins[0] - proteins);

        log.debug("Deviation from expected ranges:");
        log.debug("Kilocalories: {}", kilocaloriesDeviation);
        log.debug("Fats: {}", fatsDeviation);
        log.debug("Carbohydrates: {}", carbohydratesDeviation);
        log.debug("Proteins: {}", proteinsDeviation);

        return withinRange;
    }


    public void setMealRequirements(Double bmr, Double mealCoef) {
        double maxCalories = bmr * mealCoef * PLUS_FIVE_PERCENT;
        double minCalories = bmr * mealCoef * MINUS_FIVE_PERCENT;

        expectedKilocalories = new Double[]{minCalories, maxCalories};
        expectedCarbohydrates = new Double[]{minCalories * CARBOHYDRATES_COEF, maxCalories * CARBOHYDRATES_COEF};
        expectedFats = new Double[]{minCalories * FATS_COEF, maxCalories * FATS_COEF};
        expectedProteins = new Double[]{minCalories * PROTEINS_COEF, maxCalories * PROTEINS_COEF};

    }

    public void setMealDetails(List<Recipe> newRecipes) {
        recipes.addAll(newRecipes);
        boolean isRecipesEmpty = newRecipes.isEmpty();
        calories = isRecipesEmpty ? 0 : newRecipes.stream().mapToDouble(Recipe::getKilocalories).sum();
        fats = isRecipesEmpty ? 0 : newRecipes.stream().mapToDouble(Recipe::getFats).sum();
        carbohydrates = isRecipesEmpty ? 0 : newRecipes.stream().mapToDouble(Recipe::getCarbohydrates).sum();
        proteins = isRecipesEmpty ? 0 : newRecipes.stream().mapToDouble(Recipe::getProteins).sum();
        sumNutrients();
    }


}
