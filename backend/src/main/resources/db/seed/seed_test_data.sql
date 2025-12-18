-- Test Data Seed Script
-- This script creates test users and products for development/testing purposes
-- 
-- Test user credentials:
-- User 1: alice@example.com / password123
-- User 2: bob@example.com / password123
-- User 3: charlie@example.com / password123

-- Clear existing data (optional - comment out if you want to keep existing data)
-- DELETE FROM products;
-- DELETE FROM users;

-- Insert test users
-- Password for all users is: password123
-- BCrypt hash generated with cost factor 12
INSERT INTO users (id, username, email, password_hash, created_at) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'alice', 'alice@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYk3H.sQK1G', NOW() - INTERVAL '30 days'),
    ('550e8400-e29b-41d4-a716-446655440002', 'bob', 'bob@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYk3H.sQK1G', NOW() - INTERVAL '25 days'),
    ('550e8400-e29b-41d4-a716-446655440003', 'charlie', 'charlie@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYk3H.sQK1G', NOW() - INTERVAL '20 days')
ON CONFLICT (email) DO NOTHING;

-- Insert test products
INSERT INTO products (id, name, description, price, stock_count, created_by, updated_by, created_at, updated_at) VALUES
    -- Electronics
    ('650e8400-e29b-41d4-a716-446655440001', 'Laptop Pro 15"', 'High-performance laptop with 16GB RAM and 512GB SSD. Perfect for developers and content creators.', 1299.99, 25, '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', NOW() - INTERVAL '29 days', NOW() - INTERVAL '2 days'),
    ('650e8400-e29b-41d4-a716-446655440002', 'Wireless Mouse', 'Ergonomic wireless mouse with precision tracking and long battery life.', 49.99, 150, '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', NOW() - INTERVAL '28 days', NOW() - INTERVAL '5 days'),
    ('650e8400-e29b-41d4-a716-446655440003', 'Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches. Tactile and clicky feedback.', 129.99, 45, '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', NOW() - INTERVAL '27 days', NOW() - INTERVAL '1 day'),
    ('650e8400-e29b-41d4-a716-446655440004', 'USB-C Hub', '7-in-1 USB-C hub with HDMI, USB 3.0, SD card reader, and power delivery.', 39.99, 200, '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', NOW() - INTERVAL '26 days', NOW() - INTERVAL '3 days'),
    ('650e8400-e29b-41d4-a716-446655440005', '27" 4K Monitor', 'Professional 4K monitor with IPS panel and HDR support. Perfect for design work.', 499.99, 18, '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', NOW() - INTERVAL '25 days', NOW() - INTERVAL '1 day'),
    
    -- Office Supplies
    ('650e8400-e29b-41d4-a716-446655440006', 'Notebook Set', 'Premium hardcover notebooks, set of 3. Dotted pages, 120 pages each.', 24.99, 75, '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', NOW() - INTERVAL '24 days', NOW() - INTERVAL '4 days'),
    ('650e8400-e29b-41d4-a716-446655440007', 'Desk Organizer', 'Bamboo desk organizer with multiple compartments for pens, notes, and accessories.', 34.99, 60, '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', NOW() - INTERVAL '23 days', NOW() - INTERVAL '2 days'),
    ('650e8400-e29b-41d4-a716-446655440008', 'Standing Desk Mat', 'Anti-fatigue standing desk mat. Ergonomic design for comfort during long work hours.', 79.99, 30, '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', NOW() - INTERVAL '22 days', NOW() - INTERVAL '6 days'),
    
    -- Audio Equipment
    ('650e8400-e29b-41d4-a716-446655440009', 'Wireless Headphones', 'Noise-cancelling over-ear headphones with 30-hour battery life.', 249.99, 40, '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', NOW() - INTERVAL '21 days', NOW() - INTERVAL '3 days'),
    ('650e8400-e29b-41d4-a716-446655440010', 'USB Microphone', 'Studio-quality USB microphone for podcasting and streaming.', 89.99, 55, '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', NOW() - INTERVAL '20 days', NOW() - INTERVAL '1 day'),
    ('650e8400-e29b-41d4-a716-446655440011', 'Bluetooth Speaker', 'Portable Bluetooth speaker with 360° sound and waterproof design.', 69.99, 85, '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', NOW() - INTERVAL '19 days', NOW() - INTERVAL '2 days'),
    
    -- Smart Home
    ('650e8400-e29b-41d4-a716-446655440012', 'Smart LED Bulbs (4-Pack)', 'WiFi-enabled color-changing LED bulbs. Control with smartphone or voice assistant.', 44.99, 120, '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', NOW() - INTERVAL '18 days', NOW() - INTERVAL '5 days'),
    ('650e8400-e29b-41d4-a716-446655440013', 'Smart Plug (2-Pack)', 'WiFi smart plugs with energy monitoring. Schedule and control devices remotely.', 29.99, 95, '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', NOW() - INTERVAL '17 days', NOW() - INTERVAL '4 days'),
    
    -- Gaming
    ('650e8400-e29b-41d4-a716-446655440014', 'Gaming Chair', 'Ergonomic gaming chair with lumbar support and adjustable armrests.', 299.99, 12, '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', NOW() - INTERVAL '16 days', NOW() - INTERVAL '7 days'),
    ('650e8400-e29b-41d4-a716-446655440015', 'Gaming Mouse Pad XL', 'Extra-large gaming mouse pad with RGB lighting and non-slip rubber base.', 39.99, 70, '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', NOW() - INTERVAL '15 days', NOW() - INTERVAL '3 days'),
    
    -- Low stock items
    ('650e8400-e29b-41d4-a716-446655440016', 'Webcam HD Pro', 'Full HD 1080p webcam with auto-focus and built-in dual microphones.', 79.99, 5, '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', NOW() - INTERVAL '14 days', NOW() - INTERVAL '1 day'),
    ('650e8400-e29b-41d4-a716-446655440017', 'Tablet Stand Adjustable', 'Aluminum tablet and phone stand with multiple viewing angles.', 24.99, 3, '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', NOW() - INTERVAL '13 days', NOW() - INTERVAL '2 days'),
    
    -- Out of stock items
    ('650e8400-e29b-41d4-a716-446655440018', 'Graphics Tablet', 'Digital drawing tablet with 8192 pressure levels and battery-free pen.', 199.99, 0, '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', NOW() - INTERVAL '12 days', NOW() - INTERVAL '1 day'),
    ('650e8400-e29b-41d4-a716-446655440019', 'Docking Station Pro', 'Thunderbolt 4 docking station with dual 4K display support.', 349.99, 0, '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', NOW() - INTERVAL '11 days', NOW() - INTERVAL '8 days'),
    
    -- Recently added items
    ('650e8400-e29b-41d4-a716-446655440020', 'Cable Management Kit', 'Complete cable management solution with clips, sleeves, and velcro ties.', 19.99, 150, '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', NOW() - INTERVAL '5 days', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- Display summary
SELECT 'Seed data loaded successfully!' as message;
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as product_count FROM products;
