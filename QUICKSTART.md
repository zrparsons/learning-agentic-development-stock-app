# Quick Start Guide

Get the Stock App up and running in 5 minutes!

## Prerequisites

Make sure you have installed:
- Docker & Docker Compose
- Java 17+
- Node.js 18+

## Step-by-Step Setup

### 1. Start the Database

```bash
docker-compose up -d
```

Wait a few seconds for PostgreSQL to be ready.

### 2. Load Test Data

**macOS/Linux:**
```bash
./seed-database.sh
```

**Windows:**
```bash
seed-database.bat
```

This creates 3 test users and 20 sample products.

### 3. Start the Backend

```bash
cd backend
./gradlew run
```

Backend will be available at `http://localhost:8080`

### 4. Start the Frontend

Open a new terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend will be available at `http://localhost:3000`

### 5. Login

Open your browser to `http://localhost:3000` and login with:

**Email:** alice@example.com  
**Password:** password123

## What You'll See

After logging in, you'll find:
- 20 pre-loaded products across various categories
- Products with different stock levels
- Some out-of-stock items to test edge cases
- Products created by different users (alice, bob, charlie)

## Try These Features

1. **View Products** - See the full catalog with stock counts
2. **Adjust Stock** - Use the +/- buttons to increase/decrease stock
3. **Add Product** - Click "Add Product" to create a new item
4. **Edit Product** - Click "Edit" on any product
5. **Delete Product** - Click "Delete" to remove a product
6. **Switch Users** - Logout and login as `bob@example.com` or `charlie@example.com`

## Troubleshooting

### Database Connection Failed
```bash
# Check if PostgreSQL is running
docker ps

# If not running, start it
docker-compose up -d
```

### Port Already in Use
- Backend (8080): Stop any other services using this port
- Frontend (3000): Stop any other services using this port
- PostgreSQL (5432): Stop any other PostgreSQL instances

### Reset Everything
```bash
# Stop everything
docker-compose down -v

# Start fresh
docker-compose up -d
./seed-database.sh

# Restart backend and frontend
```

## Next Steps

- Check out `README.md` for detailed documentation
- Review `backend/src/main/resources/db/seed/README.md` for test data details
- Explore the API endpoints at `http://localhost:8080/api`

## Support

Found an issue? Check the main README.md for more detailed setup instructions.
