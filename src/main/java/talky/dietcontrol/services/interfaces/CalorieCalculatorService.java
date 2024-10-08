package talky.dietcontrol.services.interfaces;

import talky.dietcontrol.model.dto.dailymenu.DailyMenuDTO;
import talky.dietcontrol.model.dto.dailymenu.MenuInfoDTO;

public interface CalorieCalculatorService {
    double calculateDailyCalorieNeeds(MenuInfoDTO menuInfoDTO);

    void calculateTotalParameters(DailyMenuDTO dailyMenuDTO);
}
