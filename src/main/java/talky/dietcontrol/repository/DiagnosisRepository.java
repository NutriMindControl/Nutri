package talky.dietcontrol.repository;

import org.springframework.data.repository.CrudRepository;
import talky.dietcontrol.model.entities.Diagnosis;

import java.util.List;

public interface DiagnosisRepository extends CrudRepository<Diagnosis, Long> {
     List<Diagnosis> findAll();

}
