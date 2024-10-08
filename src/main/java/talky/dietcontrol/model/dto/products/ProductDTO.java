package talky.dietcontrol.model.dto.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import talky.dietcontrol.model.dto.dailymenu.BaseDTOWithNutrientsMethods;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
