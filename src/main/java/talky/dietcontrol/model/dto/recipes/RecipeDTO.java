package talky.dietcontrol.model.dto.recipes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import talky.dietcontrol.model.dto.dailymenu.BaseDTOWithNutrientsMethods;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO implements BaseDTOWithNutrientsMethods {
    @JsonProperty("name")
    private String recipeName = null;

    @JsonProperty("id")
    private Long recipeId = null;

    @JsonProperty("cook_time_mins")
    private Integer cookTimeMins = null;

    @JsonProperty("prep_time_mins")
    private String prepTimeMins = null;

    @JsonProperty("kilocalories")
    private Double kilocalories = null;

    @JsonProperty("proteins")
    private Double proteins = null;

    @JsonProperty("fats")
    private Double fats = null;

    @JsonProperty("carbohydrates")
    private Double carbohydrates = null;

    @JsonProperty("ingredients_distributions")
    @Valid
    private List<IngredientsDistributionDto> ingredientsDistributions = null;

    @JsonProperty("recipe_steps")
    @Valid
    private List<StepDto> steps;

    @Override
    public Double getCalories() {
        return kilocalories;
    }
}