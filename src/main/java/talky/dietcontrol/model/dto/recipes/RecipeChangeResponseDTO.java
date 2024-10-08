package talky.dietcontrol.model.dto.recipes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import talky.dietcontrol.model.dto.dailymenu.TotalParamsDTO;

@Data
public class RecipeChangeResponseDTO {
    @JsonProperty("recipe")
    private RecipeDTO recipe;

    @JsonProperty("total_params")
    private TotalParamsDTO totalParams;
}
