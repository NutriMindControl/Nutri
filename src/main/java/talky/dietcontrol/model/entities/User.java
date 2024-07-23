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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "height_cm", nullable = false)
    private Integer heightCm;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "diagnosis", nullable = false)
    private String diagnosis;

    @Column(name = "physical_activity_level", nullable = false, columnDefinition = "VARCHAR DEFAULT 'Умеренная'")
    private String physicalActivityLevel;

    @Column(name = "bmi")
    private Double bmi;

    @Column(name = "daily_calorie_needs", nullable = false)
    private Double dailyCalorieNeeds;

    @Column(name = "daily_protein_needs")
    private Double dailyProteinNeeds;

    @Column(name = "daily_fat_needs")
    private Double dailyFatNeeds;

    @Column(name = "daily_carbohydrate_needs")
    private Double dailyCarbohydrateNeeds;

}
