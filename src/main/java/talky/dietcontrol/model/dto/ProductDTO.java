package talky.dietcontrol.model.dto;

import lombok.Data;

@Data
public class ProductDTO implements BaseDTOWithNutrientsMethods {

    private Long productId;
    private String productName;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Double calories;
    private Double serving;
    private String categories;
}
