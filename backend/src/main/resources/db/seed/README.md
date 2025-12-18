# Test Data Seed Information

This directory contains SQL scripts for loading test data into the database for development and testing purposes.

## Contents

### seed_test_data.sql

This script populates the database with:

#### Test Users (3)
All users have the password: `password123`

| Username | Email | Created |
|----------|-------|---------|
| alice | alice@example.com | 30 days ago |
| bob | bob@example.com | 25 days ago |
| charlie | charlie@example.com | 20 days ago |

#### Test Products (20)

The script creates 20 diverse products across multiple categories:

**Electronics (5)**
- Laptop Pro 15" ($1,299.99, stock: 25)
- Wireless Mouse ($49.99, stock: 150)
- Mechanical Keyboard ($129.99, stock: 45)
- USB-C Hub ($39.99, stock: 200)
- 27" 4K Monitor ($499.99, stock: 18)

**Office Supplies (3)**
- Notebook Set ($24.99, stock: 75)
- Desk Organizer ($34.99, stock: 60)
- Standing Desk Mat ($79.99, stock: 30)

**Audio Equipment (3)**
- Wireless Headphones ($249.99, stock: 40)
- USB Microphone ($89.99, stock: 55)
- Bluetooth Speaker ($69.99, stock: 85)

**Smart Home (2)**
- Smart LED Bulbs 4-Pack ($44.99, stock: 120)
- Smart Plug 2-Pack ($29.99, stock: 95)

**Gaming (2)**
- Gaming Chair ($299.99, stock: 12)
- Gaming Mouse Pad XL ($39.99, stock: 70)

**Low Stock Items (2)**
- Webcam HD Pro ($79.99, stock: 5) ⚠️
- Tablet Stand Adjustable ($24.99, stock: 3) ⚠️

**Out of Stock (2)**
- Graphics Tablet ($199.99, stock: 0) ❌
- Docking Station Pro ($349.99, stock: 0) ❌

**Recently Added (1)**
- Cable Management Kit ($19.99, stock: 150)

## Usage

### Using the Shell Scripts (Recommended)

**macOS/Linux:**
```bash
./seed-database.sh
```

**Windows:**
```bash
seed-database.bat
```

### Using psql Directly

```bash
PGPASSWORD=stockapp psql -h localhost -p 5432 -U stockapp -d stockapp -f backend/src/main/resources/db/seed/seed_test_data.sql
```

### Using Docker

If your database is running in Docker:

```bash
docker exec -i stock-app-postgres psql -U stockapp -d stockapp < backend/src/main/resources/db/seed/seed_test_data.sql
```

## Notes

- The script uses `ON CONFLICT DO NOTHING` to prevent duplicate entries
- All passwords are hashed with BCrypt (cost factor 12)
- Products have realistic created/updated timestamps spanning 30 days
- Products are distributed among the three test users for variety
- The script will not fail if data already exists

## Password Hash

The BCrypt hash used for all test users is:
```
$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYk3H.sQK1G
```

This corresponds to the password: `password123`

## Resetting the Database

If you want to completely reset the database:

```bash
# Stop the application
# Then run:
docker-compose down -v
docker-compose up -d

# Wait for the database to be ready, then run the seed script
./seed-database.sh
```

This will:
1. Remove all data including volumes
2. Start fresh PostgreSQL container
3. Auto-run Flyway migrations
4. Load test data
