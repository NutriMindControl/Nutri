package talky.dietcontrol.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DiagnosisDTO {
    @JsonProperty("diagnose_id")
    private Long diagnoseId;
    @JsonProperty("code")
    private Long code;
    @JsonProperty("icd10code")
    private String icd10Code;
    @JsonProperty("diagnosis_description")
    private String diagnosisDescription;
}