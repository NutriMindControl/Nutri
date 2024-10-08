package talky.dietcontrol.model.dto.products;

import lombok.Data;

@Data

public class ProductCategoryProhibitionDTO {

    private Long id;
    private Long productId;
    private Integer categoryNumber;
}