package talky.dietcontrol.model.entities;

import lombok.Getter;

@Getter
public enum MealType {
    BREAKFAST("Завтрак", 0.3),
    LUNCH("Обед", 0.4),
    DINNER("Ужин", 0.3);

    private final String category;
    private final double part;

    MealType(String category, double part) {
        this.category = category;
        this.part = part;
    }
}