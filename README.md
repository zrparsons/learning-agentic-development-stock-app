# Stock App - Product Catalog

A full-stack product catalog application built with Kotlin (Ktor) backend, React TypeScript frontend, and PostgreSQL database.

## Features

- User authentication (register/login) with JWT tokens
- Product CRUD operations (Create, Read, Update, Delete)
- Secure API endpoints with authentication middleware
- Modern React UI with TypeScript
- PostgreSQL database with Exposed ORM

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

### 2. Backend Setup

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

### 3. Frontend Setup

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
│       └── application.conf        # Application config
├── frontend/
│   ├── src/
│   │   ├── components/             # React components
│   │   ├── contexts/               # React contexts
│   │   ├── services/               # API client
│   │   ├── types/                  # TypeScript types
│   │   └── App.tsx                 # Main app component
│   └── package.json
├── docker-compose.yml              # PostgreSQL setup
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

## Notes

- The JWT secret in `application.conf` should be changed in production
- Database credentials in `docker-compose.yml` should be changed for production
- The application creates database tables automatically on first run

