package talky.dietcontrol.model.dto.products;

import lombok.Data;

@Data

public class FavoriteProductsDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private Boolean isLiked;
}