CREATE TABLE IF NOT EXISTS allowed_products
(
    product_id BIGINT NOT NULL,
    PRIMARY KEY (product_id)
);

ALTER TABLE diagnosis
    ALTER COLUMN code TYPE BIGINT USING code::bigint;
