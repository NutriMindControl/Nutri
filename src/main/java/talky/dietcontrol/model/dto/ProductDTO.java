package talky.dietcontrol.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductDTO implements BaseDTOWithNutrientsMethods {
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_name")
    private String productName;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Double calories;
    private Double serving;
    private String categories;
}
