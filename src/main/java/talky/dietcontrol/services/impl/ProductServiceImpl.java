package talky.dietcontrol.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.optim.PointValuePair;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import talky.dietcontrol.model.dto.dailymenu.MealDTO;
import talky.dietcontrol.model.dto.products.ProductDTO;
import talky.dietcontrol.model.entities.Meal;
import talky.dietcontrol.model.entities.Product;
import talky.dietcontrol.model.entities.Recipe;
import talky.dietcontrol.repository.ProductsRepository;
import talky.dietcontrol.services.interfaces.ProductService;

import java.util.*;

import static talky.dietcontrol.services.impl.CalorieCalculatorServiceImpl.solveNutrientBalance;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    public ProductServiceImpl(ProductsRepository allowedProductsRepo, ModelMapper modelMapper) {
        this.allowedProductsRepo = allowedProductsRepo;
        this.modelMapper = modelMapper;
    }

    private static ProductDTO getProductDTO(ProductDTO product,
                                            double gramsNeeded) {

        ProductDTO productDTO = new ProductDTO();

        productDTO.setCarbohydrates(product.getCarbohydrates() / product.getServing() * gramsNeeded);
        productDTO.setCalories(gramsNeeded * (product.getCalories() / product.getServing()));
        productDTO.setProteins(gramsNeeded * (product.getProteins() / product.getServing()));
        productDTO.setFats(gramsNeeded * (product.getFats() / product.getServing()));
        productDTO.setServing(gramsNeeded);
        productDTO.setProductName(product.getProductName());
        productDTO.setProductId(product.getProductId());
        productDTO.setCategories(product.getCategories());
        return productDTO;
    }

    private final ProductsRepository allowedProductsRepo;
    private final ModelMapper modelMapper;


    private final Random random = new Random();

    static void updateFixedProducts(List<ProductDTO> fixedProducts, List<ProductDTO> products, PointValuePair solution) {
        int numProducts = products.size();
        fixedProducts.clear();
        for (int i = 0; i < numProducts; i++) {
            ProductDTO productDTO = getProductDTO(products.get(i), solution.getPoint()[i]);
            fixedProducts.add(productDTO);
        }
    }

    public void fillMealWithProducts(Meal
                                             meal, List<ProductDTO> products) {

        final List<ProductDTO> mutableList = new ArrayList<>(products);
        log.info("Started filling meal with products");
        List<ProductDTO> adjustedProducts = new ArrayList<>();
        double remainingCalories = meal.getExpectedKilocalories()[1] - meal.getCalories();
        double remainingProtein = meal.getExpectedProteins()[1] - meal.getProteins();
        double remainingFat = meal.getExpectedFats()[1] - meal.getFats();
        double remainingCarbs = meal.getExpectedCarbohydrates()[1] - meal.getCarbohydrates();
        boolean solved;

        if (!(remainingFat > 0 && remainingCalories > 0 && remainingProtein > 0 && remainingCarbs > 0)) {
            return;
        }
        do {
            Collections.shuffle(mutableList, random);
            int numberOfProductsToSelect = random.nextInt(Math.min(4, products.size())) + 1;
            List<ProductDTO> productDTOS = mutableList.subList(0, numberOfProductsToSelect);
            solved = solveNutrientBalance(adjustedProducts, productDTOS, remainingCalories, remainingProtein, remainingFat, remainingCarbs);
            log.debug(productDTOS.stream().map(ProductDTO::getProductName).toList().toString());
            if (numberOfProductsToSelect == products.size()) break;
        } while (!solved);

        log.info("Products with dishes were added: {}", adjustedProducts.stream().map(ProductDTO::getProductName).toList());
        meal.setProducts(adjustedProducts);
        meal.sumNutrients();
    }

    @Override
    public List<ProductDTO> findAllowedProductsForSelfConsumptionForDiagnose(Long diagnoseId) {
        List<Product> allowedProducts = allowedProductsRepo.findAllowedProducts(diagnoseId);
        return allowedProducts.stream().map(element -> modelMapper.map(element, ProductDTO.class)).toList();
    }

    public void removeUsedProducts(List<ProductDTO> mutableList, List<Recipe> mutableListRecipes, MealDTO meal) {
        mutableList.removeIf(mutableItem ->
                meal.getProducts().stream()
                        .anyMatch(mealItem -> Objects.equals(mealItem.getProductId(), mutableItem.getProductId()))
        );
        mutableListRecipes.removeIf(mutableItem ->
                meal.getRecipes().stream()
                        .anyMatch(mealItem -> Objects.equals(mealItem.getRecipeId(), mutableItem.getId()))
        );
    }


}
