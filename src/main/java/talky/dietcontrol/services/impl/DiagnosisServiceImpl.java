package talky.dietcontrol.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import talky.dietcontrol.exceptions.NotFoundException;
import talky.dietcontrol.model.dto.user.DiagnosisDTO;
import talky.dietcontrol.model.entities.Diagnosis;
import talky.dietcontrol.repository.DiagnosisRepository;
import talky.dietcontrol.services.interfaces.DiagnosisService;

import java.util.List;

@Slf4j
@Service
public class DiagnosisServiceImpl implements DiagnosisService {
    private final DiagnosisRepository repository;
    private final ModelMapper modelMapper;

    public DiagnosisServiceImpl(DiagnosisRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<List<DiagnosisDTO>> getDiagnosis() {
        List<Diagnosis> diagnosisList = repository.findAll();
        List<DiagnosisDTO> diagnosisDTOS = diagnosisList.stream().map(
                element -> modelMapper.map(element, DiagnosisDTO.class)).toList();
        return ResponseEntity.ok(diagnosisDTOS);
    }

    @Override
    public Diagnosis fetchDiagnosis(Long diagnosisId) {
        log.debug("Fetching diagnosis with ID: {}", diagnosisId);
        Diagnosis diagnosis = repository.findById(diagnosisId)
                .orElseThrow(() -> new NotFoundException("Diagnosis not found"));
        log.debug("Diagnosis fetched: {}", diagnosis);
        return diagnosis;
    }



}
