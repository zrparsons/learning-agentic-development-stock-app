-- Add columns as nullable first
ALTER TABLE products
ADD COLUMN created_by UUID REFERENCES users(id),
ADD COLUMN updated_by UUID REFERENCES users(id);

-- Update existing products to use the first user in the system
-- (or create a system/admin user if needed)
UPDATE products
SET created_by = (SELECT id FROM users ORDER BY created_at LIMIT 1),
    updated_by = (SELECT id FROM users ORDER BY created_at LIMIT 1)
WHERE created_by IS NULL;

-- Now make the columns NOT NULL
ALTER TABLE products
ALTER COLUMN created_by SET NOT NULL,
ALTER COLUMN updated_by SET NOT NULL;
