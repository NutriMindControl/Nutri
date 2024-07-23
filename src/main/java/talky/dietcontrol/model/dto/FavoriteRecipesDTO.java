package talky.dietcontrol.model.dto;

import lombok.Data;

@Data
public class FavoriteRecipesDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private Boolean isLiked;

}