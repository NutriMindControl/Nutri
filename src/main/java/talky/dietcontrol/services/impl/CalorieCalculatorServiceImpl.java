package talky.dietcontrol.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.springframework.stereotype.Service;
import talky.dietcontrol.model.dto.dailymenu.DailyMenuDTO;
import talky.dietcontrol.model.dto.dailymenu.MealDTO;
import talky.dietcontrol.model.dto.dailymenu.MenuInfoDTO;
import talky.dietcontrol.model.dto.products.ProductDTO;
import talky.dietcontrol.model.dto.user.Gender;
import talky.dietcontrol.model.dto.user.PhysicalActivityLevel;
import talky.dietcontrol.services.interfaces.CalorieCalculatorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static talky.dietcontrol.services.impl.DailyMenuServiceImpl.MINUS_FIVE_PERCENT;
import static talky.dietcontrol.services.impl.DailyMenuServiceImpl.PLUS_FIVE_PERCENT;
import static talky.dietcontrol.services.impl.ProductServiceImpl.updateFixedProducts;

@Slf4j
@Service
public class CalorieCalculatorServiceImpl implements CalorieCalculatorService {

    public static final double CALORIES_COEF = 0.9;

    public double calculateDailyCalorieNeeds(MenuInfoDTO menuInfoDTO) {
        double weight = menuInfoDTO.getWeight();
        double height = menuInfoDTO.getHeight();
        long age = menuInfoDTO.getAge();

        Gender gender = Gender.fromValue(menuInfoDTO.getGender());
        PhysicalActivityLevel physicalActivityLevel = PhysicalActivityLevel.fromDescription(menuInfoDTO.getPhysicalActivityLevel());

        double bmr;

        if (gender == Gender.MALE) {
            if (age <= 3) {
                bmr = (60.9 * weight) - 54;
            } else if (age <= 10) {
                bmr = (22.7 * weight) + 495;
            } else if (age <= 18) {
                bmr = (17.5 * weight) + 651;
            } else {
                bmr = 10 * weight + 6.25 * height - 5 * age + 5;
            }
        } else {
            if (age <= 3) {
                bmr = (60.0 * weight) - 51;
            } else if (age <= 10) {
                bmr = (22.5 * weight) + 499;
            } else if (age <= 18) {
                bmr = (12.2 * weight) + 746;
            } else {
                bmr = 10 * weight + 6.25 * height - 5 * age - 161;
            }
        }

        double dailyCalorieNeeds = bmr * physicalActivityLevel.getMultiplier() * CALORIES_COEF;

        return Math.round(dailyCalorieNeeds);

    }

    static boolean solveNutrientBalance(List<ProductDTO> fixedProducts, List<ProductDTO> products, double remainingCalories, double remainingProtein, double remainingFat, double remainingCarbs) {

        int numProducts = products.size();

        double[] objectiveCoefficients = new double[numProducts];
        Arrays.fill(objectiveCoefficients, 1);

        double[] caloriesCoefficients = new double[numProducts];
        double[] proteinCoefficients = new double[numProducts];
        double[] fatCoefficients = new double[numProducts];
        double[] carbsCoefficients = new double[numProducts];

        fillNutrientCoefficients(products, caloriesCoefficients, proteinCoefficients, fatCoefficients, carbsCoefficients);

        double minConstraint = MINUS_FIVE_PERCENT;
        double maxConstraint = PLUS_FIVE_PERCENT;
        double maxPercentage = 0.05;
        double minPercentage = 0.05;

        SimplexSolver solver = new SimplexSolver();
        while (maxConstraint < 100 && minConstraint > 10e-20) {
            try {
                List<LinearConstraint> initialConstraints = createInitialConstraints(numProducts);
                List<LinearConstraint> constraints = new ArrayList<>(initialConstraints);

                constraints.add(new LinearConstraint(caloriesCoefficients, Relationship.LEQ, remainingCalories));

                setConstraintForLinearSolver(remainingProtein, constraints, proteinCoefficients, minConstraint, maxConstraint);
                setConstraintForLinearSolver(remainingFat, constraints, fatCoefficients, minConstraint, maxConstraint);
                setConstraintForLinearSolver(remainingCarbs, constraints, carbsCoefficients, minConstraint, maxConstraint);

                LinearObjectiveFunction function = new LinearObjectiveFunction(objectiveCoefficients, 0);
                LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);
                PointValuePair solution = solver.optimize(function, constraintSet);
                updateFixedProducts(fixedProducts, products, solution);

                return true;
            } catch (Exception ignored) {
                maxConstraint += maxPercentage;
                minConstraint -= minPercentage;
                maxPercentage *= 2;
                minPercentage /= 2;
            }
        }
        return false;
    }

    private static void fillNutrientCoefficients(List<ProductDTO> products, double[] caloriesCoefficients, double[] proteinCoefficients, double[] fatCoefficients, double[] carbsCoefficients) {
        int numProducts = products.size();
        for (int i = 0; i < numProducts; i++) {
            ProductDTO product = products.get(i);
            double servings = product.getServing() == null ? 4 : product.getServing();
            caloriesCoefficients[i] = product.getCalories() / servings;
            proteinCoefficients[i] = product.getProteins() / servings;
            fatCoefficients[i] = product.getFats() / servings;
            carbsCoefficients[i] = product.getCarbohydrates() / servings;
        }
    }

    private static List<LinearConstraint> createInitialConstraints(int numProducts) {
        List<LinearConstraint> constraints = new ArrayList<>();
        for (int i = 0; i < numProducts; i++) {
            double[] singleVariableConstraint = new double[numProducts];
            singleVariableConstraint[i] = 1;
            constraints.add(new LinearConstraint(singleVariableConstraint, Relationship.GEQ, 25));
        }
        return constraints;
    }


    private static void setConstraintForLinearSolver(double remainingProtein, List<LinearConstraint> constraints, double[] proteinCoefficients, double minConstraint, double maxConstraint) {
        constraints.add(new LinearConstraint(proteinCoefficients, Relationship.LEQ, remainingProtein * maxConstraint));
        constraints.add(new LinearConstraint(proteinCoefficients, Relationship.GEQ, remainingProtein * minConstraint));
    }


    public void calculateTotalParameters(DailyMenuDTO dailyMenuDTO) {
        MealDTO breakfast = dailyMenuDTO.getBreakfastMeals();
        MealDTO lunch = dailyMenuDTO.getLunchMeals();
        MealDTO dinner = dailyMenuDTO.getDinnerMeals();

        double totalCalories = Stream.of(breakfast, lunch, dinner).mapToDouble(MealDTO::getCalories).sum();

        double dailyProteins = Stream.of(breakfast, lunch, dinner).mapToDouble(MealDTO::getProteins).sum();

        double dailyFats = Stream.of(breakfast, lunch, dinner).mapToDouble(MealDTO::getFats).sum();

        double dailyCarbohydrates = Stream.of(breakfast, lunch, dinner).mapToDouble(MealDTO::getCarbohydrates).sum();

        dailyMenuDTO.getTotalParams().setTotalCalories(totalCalories);
        dailyMenuDTO.getTotalParams().setTotalProteins(dailyProteins);
        dailyMenuDTO.getTotalParams().setTotalFats(dailyFats);
        dailyMenuDTO.getTotalParams().setTotalCarbohydrates(dailyCarbohydrates);
    }

}
