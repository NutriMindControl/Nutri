package talky.dietcontrol.model.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "measure_units")
public class MeasureUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @Column(name = "conversion_to_grams")
    private Long conversionToGrams;


    @OneToMany(mappedBy = "unit")
    @ToString.Exclude
    private List<IngredientsDistribution> ingredientsDistributions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasureUnit unit)) return false;
        return getId() == unit.getId();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
