package talky.dietcontrol.model.dto.dailymenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalParamsDTO {
    @JsonProperty("total_calories")
    private Double totalCalories;

    @JsonProperty("total_fats")
    private Double totalFats;

    @JsonProperty("total_proteins")
    private Double totalProteins;

    @JsonProperty("total_carbohydrates")
    private Double totalCarbohydrates;

    @JsonProperty("required_calories")
    private Double requiredCalories;

    @JsonProperty("daily_protein_needs")
    private Double dailyProteinNeeds;

    @JsonProperty("daily_fat_needs")
    private Double dailyFatNeeds;

    @JsonProperty("daily_carbohydrate_needs")
    private Double dailyCarbohydrateNeeds;

    @JsonProperty("imt")
    private Double imt;

    public TotalParamsDTO(double requiredCalories, double dailyProteinNeeds, double dailyFatNeeds, double dailyCarbohydrateNeeds, double imt) {
        this.requiredCalories = requiredCalories;
        this.dailyProteinNeeds = dailyProteinNeeds;
        this.dailyFatNeeds = dailyFatNeeds;
        this.dailyCarbohydrateNeeds = dailyCarbohydrateNeeds;
        this.imt = imt;
    }
}