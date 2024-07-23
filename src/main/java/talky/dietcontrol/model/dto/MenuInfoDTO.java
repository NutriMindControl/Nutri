package talky.dietcontrol.model.dto;

import lombok.Data;

@Data
public class MenuInfoDTO {
    private Double height;
    private Double weight;

    private Long diagnoseId;
    private Long age;
    private String gender;
    private String physicalActivityLevel;

}
