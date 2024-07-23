CREATE TABLE IF NOT EXISTS products
(
    product_name          VARCHAR(64) NOT NULL UNIQUE,
    product_id            BIGINT PRIMARY KEY,
    daily_dose            BIGINT,
    water                 NUMERIC,
    protein               NUMERIC,
    fat                   NUMERIC,
    sfa                   NUMERIC,
    cholesterol           NUMERIC,
    pufa                  NUMERIC,
    starch                NUMERIC,
    carbohydrates         NUMERIC,
    dietary_fiber         NUMERIC,
    organic_acids         NUMERIC,
    ash                   NUMERIC,
    sodium                NUMERIC,
    potassium             NUMERIC,
    calcium               NUMERIC,
    magnesium             NUMERIC,
    phosphorus            NUMERIC,
    iron                  NUMERIC,
    retinol               NUMERIC,
    carotene              NUMERIC,
    retinol_equivalent    NUMERIC,
    tocopherol_equivalent NUMERIC,
    thiamine              NUMERIC,
    riboflavin            NUMERIC,
    niacin                NUMERIC,
    niacin_equivalent     NUMERIC,
    ascorbic_acid         NUMERIC,
    energy_value          NUMERIC,
    serving               NUMERIC,
    categories            VARCHAR     NOT NULL,
    subcategories         VARCHAR,
    subcategories2        VARCHAR,
    subcategories3        VARCHAR,
    subcategories4        VARCHAR
);

CREATE TABLE IF NOT EXISTS diagnosis
(
    diagnose_id           BIGINT PRIMARY KEY,
    code                  VARCHAR NOT NULL,
    icd_10_code           VARCHAR,
    diagnosis_description VARCHAR
);

CREATE TABLE IF NOT EXISTS diagnosis_synonyms
(
    diagnose_id        BIGINT,
    diagnosis_synonyms VARCHAR,
    PRIMARY KEY (diagnose_id, diagnosis_synonyms)
);


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

CREATE TEMP TABLE IF NOT EXISTS temp_nutrition_table
(
    product_id BIGINT PRIMARY KEY,
    zero       BOOLEAN,
    one        BOOLEAN,
    two        BOOLEAN,
    three      BOOLEAN
);

CREATE TABLE IF NOT EXISTS product_category_prohibitions
(
    id              BIGINT NOT NULL UNIQUE,
    product_id      BIGINT,
    category_number INT    NOT NULL
);


COPY temp_nutrition_table (product_id, zero, one, two, three)
    FROM '/home/alina/dietcontrol/src/main/resources/data/NutriMind_productsAllowed.csv' DELIMITER ',' CSV HEADER;


INSERT INTO product_category_prohibitions (id, product_id, category_number)
SELECT row_number() OVER () AS id, product_id, category_number
FROM (SELECT product_id, 0 AS category_number
      FROM temp_nutrition_table
      WHERE NOT zero
      UNION ALL
      SELECT product_id, 1 AS category_number
      FROM temp_nutrition_table
      WHERE NOT one
      UNION ALL
      SELECT product_id, 2 AS category_number
      FROM temp_nutrition_table
      WHERE NOT two
      UNION ALL
      SELECT product_id, 3 AS category_number
      FROM temp_nutrition_table
      WHERE NOT three) AS combined_data;


CREATE TABLE IF NOT EXISTS users
(
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    age                      INT         NOT NULL,
    gender                   VARCHAR(10) NOT NULL CHECK (Gender IN ('Муж.', 'Жен.')),
    height_cm                INT         NOT NULL CHECK (height_cm >= 1 AND height_cm <= 254),
    weight_kg                NUMERIC     NOT NULL CHECK (weight_kg >= 1 AND weight_kg <= 254),
    diagnosis                VARCHAR     NOT NULL DEFAULT 12,
    physical_activity_level  VARCHAR DEFAULT 'Умеренная' CHECK (
        physical_activity_level IN (
                                    'Отсутствие физической активности',
                                    'Низкая',
                                    'Умеренная',
                                    'Выше среднего',
                                    'Высокая',
                                    'Очень высокая'
            )
        ),
    bmi                      NUMERIC,
    daily_calorie_needs      NUMERIC     NOT NULL,
    daily_protein_needs      NUMERIC,
    daily_fat_needs          NUMERIC,
    daily_carbohydrate_needs NUMERIC
);

CREATE TABLE IF NOT EXISTS favorite_products
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    INT REFERENCES users (id),
    product_id BIGINT REFERENCES products,
    is_liked   BOOLEAN DEFAULT NULL
);


CREATE TABLE IF NOT EXISTS favorite_recipes
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT REFERENCES users (id),
    product_id BIGINT REFERENCES products,
    is_liked   BOOLEAN DEFAULT NULL
);
