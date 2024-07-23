package talky.dietcontrol.model.dto;

import java.util.Arrays;
import java.util.List;

public enum PhysicalActivityLevel {
    NONE("Отсутствие физической активности", 1.0),
    LOW("Низкая", 1.2),
    MODERATE("Умеренная", 1.375),
    ABOVE_AVERAGE("Выше среднего", 1.55),
    HIGH("Высокая", 1.725),
    VERY_HIGH("Очень высокая", 1.9);

    private final String description;
    private final double multiplier;

    PhysicalActivityLevel(String description, double multiplier) {
        this.description = description;
        this.multiplier = multiplier;
    }

    public String getDescription() {
        return description;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public static PhysicalActivityLevel fromDescription(String description) {
        for (PhysicalActivityLevel level : PhysicalActivityLevel.values()) {
            if (level.description.equalsIgnoreCase(description)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown physical activity level: " + description);
    }

    public static List<String> getAllDescriptions() {
        return Arrays.stream(PhysicalActivityLevel.values())
                .map(PhysicalActivityLevel::getDescription)
                .toList();
    }
}

