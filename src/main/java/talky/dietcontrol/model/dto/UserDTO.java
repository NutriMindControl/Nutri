package talky.dietcontrol.model.dto;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private Integer age;
    private String gender;
    private Integer heightCm;
    private Double weightKg;
    private String diagnosis;
    private String physicalActivityLevel;
    private Double bmi;
    private Double dailyCalorieNeeds;
    private Double dailyProteinNeeds;
    private Double dailyFatNeeds;
    private Double dailyCarbohydrateNeeds;

}