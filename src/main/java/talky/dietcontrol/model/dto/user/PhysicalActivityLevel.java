package talky.dietcontrol.model.dto.user;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PhysicalActivityLevel {
    NONE("Отсутствие физической активности", 1.0),
    LOW("Низкая физическая активность", 1.2),
    MODERATE("Умеренная физическая активность", 1.375),
    ABOVE_AVERAGE("Физическая активность выше среднего", 1.55),
    HIGH("Высокая физическая активность", 1.725),
    VERY_HIGH("Очень высокая физическая активность", 1.9);

    private final String description;
    private final double multiplier;

    PhysicalActivityLevel(String description, double multiplier) {
        this.description = description;
        this.multiplier = multiplier;
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

