package talky.dietcontrol.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import talky.dietcontrol.config.Constants;
import talky.dietcontrol.model.dto.DiagnosisDTO;
import talky.dietcontrol.model.dto.MenuInfoDTO;
import talky.dietcontrol.model.dto.RecipeDTO;

import java.util.List;

@RequestMapping(Constants.BASE_API_PATH + "/diagnosis")
@Validated
public interface DiagnosisApi {

    @GetMapping
    ResponseEntity<List<DiagnosisDTO>> getDiagnosis();

}
