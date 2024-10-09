CREATE TABLE IF NOT EXISTS recipes
(
    id             bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name           varchar(128) NOT NULL,
    cook_time_mins integer,
    prep_time_mins varchar(128),
    kilocalories   float,
    proteins       float,
    fats           float,
    carbohydrates  float
        CHECK (
            (cook_time_mins > 0)
                AND ((kilocalories IS NULL) OR
                     ((kilocalories IS NOT NULL) AND (kilocalories > 0)))
                AND
            ((proteins IS NULL) OR ((proteins IS NOT NULL) AND (proteins > 0)))
                AND ((fats IS NULL) OR ((fats IS NOT NULL) AND (fats > 0)))
                AND ((carbohydrates IS NULL) OR
                     ((carbohydrates IS NOT NULL) AND (carbohydrates > 0)))
            )
);

CREATE TABLE IF NOT EXISTS measure_units
(
    id                  bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                varchar(32) NOT NULL UNIQUE,
    conversion_to_grams BIGINT
);

COPY measure_units (id, name, conversion_to_grams)
    FROM '/csv-data/NutriMind_measures.csv' DELIMITER ',' CSV HEADER;

CREATE TABLE IF NOT EXISTS categories
(
    id   bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(128) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories_distribution
(
    category_id bigint NOT NULL REFERENCES categories ON DELETE CASCADE,
    recipe_id   bigint NOT NULL REFERENCES recipes ON DELETE CASCADE,
    PRIMARY KEY (category_id, recipe_id)
);


COPY recipes (id, name, cook_time_mins, prep_time_mins, kilocalories, proteins,
              fats, carbohydrates)
    FROM '/csv-data/NutriMind_Recipes.csv' DELIMITER ',' CSV HEADER;
CREATE TABLE IF NOT EXISTS recipe_steps
(
    id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description text    NOT NULL,
    step_num    integer NOT NULL,
    recipe_id   bigint  NOT NULL REFERENCES recipes ON DELETE CASCADE,
    CHECK (
        step_num >= 0
        ),
    UNIQUE (step_num, recipe_id)
);
COPY recipe_steps (id, description, step_num, recipe_id)
    FROM '/csv-data/NutriMind_steps.csv' DELIMITER ',' CSV HEADER;

CREATE TABLE IF NOT EXISTS ingredients_distribution
(
    recipe_id          bigint NOT NULL REFERENCES recipes ON DELETE CASCADE,
    ingredient_id      bigint NOT NULL REFERENCES products,
    measure_unit_id    bigint NOT NULL REFERENCES measure_units,
    measure_unit_count float  NOT NULL,
    PRIMARY KEY (recipe_id, ingredient_id)
);

copy ingredients_distribution (ingredient_id, recipe_id, measure_unit_id,
                               measure_unit_count)
    FROM '/csv-data/NutriMind_Ingredients_distribution.csv' DELIMITER ',' CSV HEADER;


INSERT INTO categories (name)
VALUES ('Завтрак'),
       ('Обед'),
       ('Ужин');

INSERT INTO categories_distribution (category_id, recipe_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 18),
       (1, 19),
       (2, 7),
       (2, 8),
       (2, 9),
       (2, 10),
       (2, 11),
       (3, 12),
       (3, 13),
       (3, 14),
       (3, 15),
       (3, 16)