package talky.dietcontrol.model.dto.recipes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IngredientsDistributionDto {
    @JsonProperty("name")
    private String productName;

    @JsonProperty("measure_unit_name")
    private String measureUnitName;

    @JsonProperty("count")
    private Double count;

    @JsonProperty("ingredient_id")
    private Long ingredientId;
}