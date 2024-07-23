package talky.dietcontrol.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RecipeDTO implements BaseDTOWithNutrientsMethods {
    @JsonProperty("name")
    private String recipeName = null;

    @JsonProperty("id")
    private Long recipeId = null;

    @JsonProperty("cook_time_mins")
    private Integer cookTimeMins = null;

    @JsonProperty("prep_time_mins")
    private Integer prepTimeMins = null;

    @JsonProperty("servings")
    private Integer servings = null;

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

    @Override
    public Double getCalories() {
        return kilocalories;
    }
}