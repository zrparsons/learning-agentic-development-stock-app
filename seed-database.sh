#!/bin/bash

# Database Seed Script
# This script loads test data into the PostgreSQL database

set -e  # Exit on error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Database connection parameters (from docker-compose.yml)
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-stockapp}"
DB_USER="${DB_USER:-stockapp}"
DB_PASSWORD="${DB_PASSWORD:-stockapp}"

echo -e "${YELLOW}================================================${NC}"
echo -e "${YELLOW}  Stock App Database Seed Script${NC}"
echo -e "${YELLOW}================================================${NC}"
echo ""

# Check if PostgreSQL is running
echo -e "${YELLOW}Checking database connection...${NC}"
if ! PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c '\q' 2>/dev/null; then
    echo -e "${RED}Error: Cannot connect to database!${NC}"
    echo -e "${RED}Make sure PostgreSQL is running (try: docker-compose up -d)${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Database connection successful${NC}"
echo ""

# Get current counts
echo -e "${YELLOW}Current database state:${NC}"
USER_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM users;" 2>/dev/null | xargs)
PRODUCT_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM products;" 2>/dev/null | xargs)
echo -e "  Users: ${USER_COUNT}"
echo -e "  Products: ${PRODUCT_COUNT}"
echo ""

# Confirm before proceeding
read -p "Do you want to load test data? (y/n) " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Seed cancelled.${NC}"
    exit 0
fi

# Run the seed script
echo -e "${YELLOW}Loading test data...${NC}"
SEED_FILE="backend/src/main/resources/db/seed/seed_test_data.sql"

if [ ! -f "$SEED_FILE" ]; then
    echo -e "${RED}Error: Seed file not found at $SEED_FILE${NC}"
    exit 1
fi

PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "$SEED_FILE"

echo ""
echo -e "${GREEN}✓ Test data loaded successfully!${NC}"
echo ""

# Get updated counts
echo -e "${YELLOW}Updated database state:${NC}"
USER_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM users;" 2>/dev/null | xargs)
PRODUCT_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM products;" 2>/dev/null | xargs)
echo -e "  Users: ${USER_COUNT}"
echo -e "  Products: ${PRODUCT_COUNT}"
echo ""

echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}Test Users Created:${NC}"
echo -e "${GREEN}================================================${NC}"
echo -e "Email: ${YELLOW}alice@example.com${NC}   | Password: ${YELLOW}password123${NC}"
echo -e "Email: ${YELLOW}bob@example.com${NC}     | Password: ${YELLOW}password123${NC}"
echo -e "Email: ${YELLOW}charlie@example.com${NC} | Password: ${YELLOW}password123${NC}"
echo ""
echo -e "${GREEN}You can now login to the application with any of these accounts!${NC}"
echo ""
