-- Функция для расчета ИМТ
CREATE OR REPLACE FUNCTION calculate_bmi(weight_kg NUMERIC, height_cm INT)
    RETURNS NUMERIC AS $$
BEGIN
    RETURN weight_kg / POWER(height_cm / 100.0, 2);
END;
$$ LANGUAGE plpgsql;

-- Функция для расчета суточной нормы калорий
CREATE OR REPLACE FUNCTION calculate_daily_calorie_needs(age INT, gender VARCHAR, height_cm INT, weight_kg NUMERIC, physical_activity_level VARCHAR)
    RETURNS INT AS $$
DECLARE
    bmr NUMERIC;
    pal_factor NUMERIC;
BEGIN
    IF gender = 'муж.' THEN
        bmr := 10 * weight_kg + 6.25 * height_cm - 5 * age + 5;
    ELSE
        bmr := 10 * weight_kg + 6.25 * height_cm - 5 * age - 161;
    END IF;

    CASE physical_activity_level
        WHEN 'Отсутствие физической активности' THEN pal_factor := 1;
        WHEN 'Низкая' THEN pal_factor := 1.2;
        WHEN 'Умеренная' THEN pal_factor := 1.375;
        WHEN 'Выше среднего' THEN pal_factor := 1.55;
        WHEN 'Высокая' THEN pal_factor := 1.725;
        WHEN 'Очень высокая' THEN pal_factor := 1.9;
        ELSE pal_factor := 1;
        END CASE;

    RETURN ROUND(bmr * pal_factor, 0);
END;
$$ LANGUAGE plpgsql;

-- Функция для расчета суточной нормы белков
CREATE OR REPLACE FUNCTION calculate_daily_protein_needs(daily_calorie_needs NUMERIC)
    RETURNS NUMERIC AS $$
BEGIN
    RETURN daily_calorie_needs * 0.3 / 4.1;
END;
$$ LANGUAGE plpgsql;

-- Функция для расчета суточной нормы жиров
CREATE OR REPLACE FUNCTION calculate_daily_fat_needs(daily_calorie_needs NUMERIC)
    RETURNS NUMERIC AS $$
BEGIN
    RETURN daily_calorie_needs * 0.3 / 9.3;
END;
$$ LANGUAGE plpgsql;

-- Функция для расчета суточной нормы углеводов
CREATE OR REPLACE FUNCTION calculate_daily_carbohydrate_needs(daily_calorie_needs NUMERIC)
    RETURNS NUMERIC AS $$
BEGIN
    RETURN daily_calorie_needs * 0.4 / 4.1;
END;
$$ LANGUAGE plpgsql;

-- Триггер для расчета ИМТ, суточной нормы калорий, белков, жиров и углеводов
CREATE OR REPLACE FUNCTION calculate_user_metrics()
    RETURNS TRIGGER AS $$
BEGIN
    -- Расчет и обновление ИМТ
    NEW.bmi := calculate_bmi(NEW.weight_kg, NEW.height_cm);

    -- Расчет и обновление суточной нормы калорий
    NEW.daily_calorie_needs := calculate_daily_calorie_needs(NEW.age, NEW.gender, NEW.height_cm, NEW.weight_kg, NEW.physical_activity_level);

    -- Расчет и обновление суточной нормы белков
    NEW.daily_protein_needs := calculate_daily_protein_needs(NEW.daily_calorie_needs);

    -- Расчет и обновление суточной нормы жиров
    NEW.daily_fat_needs := calculate_daily_fat_needs(NEW.daily_calorie_needs);

    -- Расчет и обновление суточной нормы углеводов
    NEW.daily_carbohydrate_needs := calculate_daily_carbohydrate_needs(NEW.daily_calorie_needs);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера на обновление таблицы users
CREATE OR REPLACE TRIGGER update_user_metrics
    BEFORE INSERT OR UPDATE OF age, gender, height_cm, weight_kg, physical_activity_level
    ON users
    FOR EACH ROW
EXECUTE FUNCTION calculate_user_metrics();
