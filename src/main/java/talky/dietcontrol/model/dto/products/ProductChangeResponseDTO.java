package talky.dietcontrol.model.dto.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import talky.dietcontrol.model.dto.dailymenu.TotalParamsDTO;

@Data
public class ProductChangeResponseDTO {
    @JsonProperty("product")
    private ProductDTO product;

    @JsonProperty("total_params")
    private TotalParamsDTO totalParams;
}
