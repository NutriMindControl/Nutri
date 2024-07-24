COPY products (product_id, product_name, daily_dose, water, protein, fat, sfa, cholesterol, pufa, starch, carbohydrates,
               dietary_fiber, organic_acids, ash, sodium, potassium, calcium, magnesium, phosphorus, iron, retinol,
               carotene, retinol_equivalent, tocopherol_equivalent, thiamine, riboflavin, niacin, niacin_equivalent,
               ascorbic_acid, energy_value, serving, categories, subcategories, subcategories2, subcategories3,
               subcategories4)
    FROM '/home/alina/dietcontrol/src/main/resources/data/NutriMind_products.csv' DELIMITER ',' CSV HEADER;

COPY diagnosis (diagnose_id, code, icd_10_code, diagnosis_description)
    FROM '/home/alina/dietcontrol/src/main/resources/data/NutriMind_diagnoses.csv' DELIMITER ',' CSV HEADER;

COPY diagnosis_synonyms (diagnose_id, diagnosis_synonyms)
    FROM '/home/alina/dietcontrol/src/main/resources/data/NutriMind_diagnosesSymptomes.csv' DELIMITER ',' CSV HEADER;


COPY temp_nutrition_table (product_id, zero, one, two, three)
    FROM '/home/alina/dietcontrol/src/main/resources/data/NutriMind_productsAllowed.csv' DELIMITER ',' CSV HEADER;
