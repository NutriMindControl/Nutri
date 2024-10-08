package talky.dietcontrol.model.dto.dailymenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MealTypeResponseDTO {
    @JsonProperty("meal")
    private MealDTO meal;

    @JsonProperty("total_params")
    private TotalParamsDTO totalParams;
}
