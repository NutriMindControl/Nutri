package talky.dietcontrol.model.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class DiagnosisDTO {

    private Long diagnoseId;
    private String code;
    private String icd10Code;
    private String diagnosisDescription;
//    private ArrayList<String> diagnosisSynonyms;
}