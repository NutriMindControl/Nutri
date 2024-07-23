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
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, unique = true)
    private String productName;

    @Column(name = "daily_dose")
    private Long dailyDose;

    @Column(name = "water")
    private Double water;

    @Column(name = "protein")
    private Double proteins;

    @Column(name = "fat")
    private Double fats;

    @Column(name = "sfa")
    private Double sfa;

    @Column(name = "cholesterol")
    private Double cholesterol;

    @Column(name = "pufa")
    private Double pufa;

    @Column(name = "starch")
    private Double starch;

    @Column(name = "carbohydrates")
    private Double carbohydrates;

    @Column(name = "dietary_fiber")
    private Double dietaryFiber;

    @Column(name = "organic_acids")
    private Double organicAcids;

    @Column(name = "ash")
    private Double ash;

    @Column(name = "sodium")
    private Double sodium;

    @Column(name = "potassium")
    private Double potassium;

    @Column(name = "calcium")
    private Double calcium;

    @Column(name = "magnesium")
    private Double magnesium;

    @Column(name = "phosphorus")
    private Double phosphorus;

    @Column(name = "iron")
    private Double iron;

    @Column(name = "retinol")
    private Double retinol;

    @Column(name = "carotene")
    private Double carotene;

    @Column(name = "retinol_equivalent")
    private Double retinolEquivalent;

    @Column(name = "tocopherol_equivalent")
    private Double tocopherolEquivalent;

    @Column(name = "thiamine")
    private Double thiamine;

    @Column(name = "riboflavin")
    private Double riboflavin;

    @Column(name = "niacin")
    private Double niacin;

    @Column(name = "niacin_equivalent")
    private Double niacinEquivalent;

    @Column(name = "ascorbic_acid")
    private Double ascorbicAcid;

    @Column(name = "energy_value")
    private Double calories;

    @Column(name = "serving")
    private Double serving;

    @Column(name = "categories", nullable = false)
    private String categories;

    @Column(name = "subcategories")
    private String subcategories;

    @Column(name = "subcategories2")
    private String subcategories2;

    @Column(name = "subcategories3")
    private String subcategories3;

    @Column(name = "subcategories4")
    private String subcategories4;

}
