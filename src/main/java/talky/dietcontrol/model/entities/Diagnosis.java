package talky.dietcontrol.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "diagnosis")
public class Diagnosis {

    @Id
    @Column(name = "diagnose_id")
    private Long diagnoseId;

    @Column(name = "code", nullable = false)
    private Long code;

    @Column(name = "icd_10_code")
    private String icd10Code;

    @Column(name = "diagnosis_description")
    private String diagnosisDescription;
}

