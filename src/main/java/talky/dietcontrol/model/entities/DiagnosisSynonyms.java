package talky.dietcontrol.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "diagnosis_synonyms",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"diagnose_id", "diagnosis_synonyms"})})
public class DiagnosisSynonyms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diagnose_id")
    private Diagnosis diagnosis;

    @Column(name = "diagnosis_synonyms")
    private String diagnosisSynonyms;

}
