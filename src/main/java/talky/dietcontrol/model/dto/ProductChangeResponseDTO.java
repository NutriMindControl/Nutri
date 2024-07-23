package talky.dietcontrol.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductChangeResponseDTO {
    @JsonProperty("product")
    private ProductDTO product;

    @JsonProperty("total_params")
    private TotalParamsDTO totalParams;
}
