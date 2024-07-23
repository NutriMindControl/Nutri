package talky.dietcontrol.services.interfaces;

import talky.dietcontrol.model.dto.DailyMenuDTO;
import talky.dietcontrol.model.dto.MenuInfoDTO;

public interface CalorieCalculatorService {
    double calculateDailyCalorieNeeds(MenuInfoDTO menuInfoDTO);

    void calculateTotalParameters(DailyMenuDTO dailyMenuDTO);
}
