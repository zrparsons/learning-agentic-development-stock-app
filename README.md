# Stock App - Product Catalog

A full-stack product catalog application built with Kotlin (Ktor) backend, React TypeScript frontend, and PostgreSQL database.

## Features

- User authentication (register/login) with JWT tokens
- Product CRUD operations (Create, Read, Update, Delete)
- Stock count management with increment/decrement buttons
- Secure API endpoints with authentication middleware
- Modern React UI with TypeScript
- PostgreSQL database with Exposed ORM
- Database migration support with Flyway
- Test data seeding scripts

## Tech Stack

### Backend
- Kotlin
- Ktor framework
- Exposed ORM
- PostgreSQL
- JWT authentication
- BCrypt password hashing

### Frontend
- React 18
- TypeScript
- React Router
- Axios
- Vite

## Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- Docker and Docker Compose
- Gradle (or use Gradle wrapper)

## Setup Instructions

### 1. Start PostgreSQL Database

```bash
docker-compose up -d
```

This will start a PostgreSQL container on port 5432 with:
- Database: `stockapp`
- User: `stockapp`
- Password: `stockapp`

### 2. Load Test Data (Optional)

To populate the database with test users and products, run the seed script:

**On macOS/Linux:**
```bash
./seed-database.sh
```

**On Windows:**
```bash
seed-database.bat
```

This will create:
- 3 test users (alice, bob, charlie) with password: `password123`
- 20 sample products across various categories

You can skip this step and create your own users/products through the application.

### 3. Backend Setup

Navigate to the backend directory:

```bash
cd backend
```

Build and run the backend:

```bash
./gradlew run
```

Or on Windows:

```bash
gradlew.bat run
```

The backend will start on `http://localhost:8080`

### 4. Frontend Setup

Navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

The frontend will start on `http://localhost:3000`

## Test Credentials

If you ran the seed script, you can login with these test accounts:

| Email | Password | Username |
|-------|----------|----------|
| alice@example.com | password123 | alice |
| bob@example.com | password123 | bob |
| charlie@example.com | password123 | charlie |

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string"
  }
  ```

- `POST /api/auth/login` - Login and receive JWT token
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```

### Products (Requires Authentication)

- `GET /api/products` - Get all products for the authenticated user
- `GET /api/products/{id}` - Get a specific product by ID
- `POST /api/products` - Create a new product
  ```json
  {
    "name": "string",
    "description": "string",
    "price": 0.0
  }
  ```
- `PUT /api/products/{id}` - Update a product
- `DELETE /api/products/{id}` - Delete a product

All product endpoints require a JWT token in the `Authorization` header:
```
Authorization: Bearer <token>
```

## Project Structure

```
stock-app/
├── backend/
│   ├── src/main/kotlin/com/stockapp/
│   │   ├── Application.kt          # Main application entry
│   │   ├── routes/                 # API route handlers
│   │   ├── models/                 # Data models
│   │   ├── services/               # Business logic
│   │   ├── database/               # Database configuration
│   │   └── plugins/                 # Ktor plugins
│   ├── build.gradle.kts            # Gradle build config
│   └── resources/
│       ├── application.conf        # Application config
│       └── db/
│           ├── migration/          # Flyway migrations
│           └── seed/               # Test data seeds
├── frontend/
│   ├── src/
│   │   ├── components/             # React components
│   │   ├── contexts/               # React contexts
│   │   ├── services/               # API client
│   │   ├── types/                  # TypeScript types
│   │   └── App.tsx                 # Main app component
│   └── package.json
├── docker-compose.yml              # PostgreSQL setup
├── seed-database.sh                # Test data loader (Unix)
├── seed-database.bat               # Test data loader (Windows)
└── README.md
```

## Development

### Backend Development

The backend uses Ktor with Exposed ORM. Database tables are automatically created on startup.

### Frontend Development

The frontend uses Vite for fast development. Hot module replacement is enabled.

## Configuration

### Backend Configuration

Edit `backend/resources/application.conf` to change:
- Server port (default: 8080)
- Database connection settings
- JWT secret (change in production!)

### Frontend Configuration

The frontend is configured to proxy API requests to `http://localhost:8080` via Vite's proxy settings in `vite.config.ts`.

## Database Seeding

The test data includes:
- **3 test users** with realistic profiles
- **20 sample products** across categories (Electronics, Office Supplies, Audio, Smart Home, Gaming)
- Products with varying stock levels (including out-of-stock items)
- Realistic timestamps and audit trails

See `backend/src/main/resources/db/seed/README.md` for detailed information about the test data.

## Notes

- The JWT secret in `application.conf` should be changed in production
- Database credentials in `docker-compose.yml` should be changed for production
- The application creates database tables automatically on first run using Flyway migrations
- Test data can be loaded at any time using the seed scripts

