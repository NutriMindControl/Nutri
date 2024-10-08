package talky.dietcontrol.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import talky.dietcontrol.api.DiagnosisApi;
import talky.dietcontrol.model.dto.user.DiagnosisDTO;
import talky.dietcontrol.services.interfaces.DiagnosisService;

import java.util.List;


@CrossOrigin(maxAge = 1440)
@RestController
public class DiagnosisApiController implements DiagnosisApi {
    private  final DiagnosisService service;

    public DiagnosisApiController(DiagnosisService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<DiagnosisDTO>> getDiagnosis() {
        return service.getDiagnosis();
    }
}
