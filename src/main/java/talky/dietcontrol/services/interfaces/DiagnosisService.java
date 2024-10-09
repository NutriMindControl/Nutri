package talky.dietcontrol.services.interfaces;

import org.springframework.http.ResponseEntity;
import talky.dietcontrol.model.dto.user.DiagnosisDTO;
import talky.dietcontrol.model.entities.Diagnosis;

import java.util.List;

public interface DiagnosisService {
     ResponseEntity<List<DiagnosisDTO>> getDiagnosis();

     Diagnosis fetchDiagnosis(Long diagnosisId);

}
