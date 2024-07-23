package talky.dietcontrol.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecipeChangeResponseDTO {
    @JsonProperty("recipe")
    private RecipeDTO recipe;

    @JsonProperty("total_params")
    private TotalParamsDTO totalParams;
}
