package talky.dietcontrol.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import talky.dietcontrol.config.Constants;
import talky.dietcontrol.model.dto.user.DiagnosisDTO;

import java.util.List;

@RequestMapping(Constants.BASE_API_PATH + "/diagnosis")
@Validated
public interface DiagnosisApi {

    @GetMapping
    ResponseEntity<List<DiagnosisDTO>> getDiagnosis();

}
