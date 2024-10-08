package talky.dietcontrol.model.dto.dailymenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MenuInfoDTO {
    @JsonProperty("height")
    private Double height;

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("diagnose_id")
    private Long diagnoseId;

    @JsonProperty("age")
    private Long age;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("physical_activity_level")
    private String physicalActivityLevel;

}
