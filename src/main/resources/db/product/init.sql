CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_products_trgm
    ON products USING gin (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_categories_trgm
    ON categories USING gin (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_sub_categories_trgm
    ON sub_categories USING gin (name gin_trgm_ops);