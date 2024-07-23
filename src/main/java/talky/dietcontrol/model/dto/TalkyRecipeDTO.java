package talky.dietcontrol.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TalkyRecipeDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("media_id")
    private Long mediaId;

    @JsonProperty("cook_time_mins")
    private Integer cookTimeMins;

    @JsonProperty("author_uid")
    private String authorUid;

    @JsonProperty("prep_time_mins")
    private Integer prepTimeMins;

    @JsonProperty("servings")
    private Integer servings;

    @JsonProperty("kilocalories")
    private Double kilocalories;

    @JsonProperty("proteins")
    private Double proteins;

    @JsonProperty("fats")
    private Double fats;

    @JsonProperty("carbohydrates")
    private Double carbohydrates;

    @JsonProperty("ingredients_distributions")
    private List<IngredientsDistributionDto> ingredientsDistributions;

    @JsonProperty("steps")
    private List<StepDto> steps;
}
