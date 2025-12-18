@echo off
REM Database Seed Script for Windows
REM This script loads test data into the PostgreSQL database

setlocal

REM Database connection parameters (from docker-compose.yml)
if "%DB_HOST%"=="" set DB_HOST=localhost
if "%DB_PORT%"=="" set DB_PORT=5432
if "%DB_NAME%"=="" set DB_NAME=stockapp
if "%DB_USER%"=="" set DB_USER=stockapp
if "%DB_PASSWORD%"=="" set DB_PASSWORD=stockapp

echo ================================================
echo   Stock App Database Seed Script
echo ================================================
echo.

REM Check if psql is available
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: psql command not found!
    echo Please install PostgreSQL client tools or ensure they are in your PATH
    exit /b 1
)

REM Check if PostgreSQL is running
echo Checking database connection...
set PGPASSWORD=%DB_PASSWORD%
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -c "\q" >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Cannot connect to database!
    echo Make sure PostgreSQL is running (try: docker-compose up -d^)
    exit /b 1
)
echo Database connection successful
echo.

REM Get current counts
echo Current database state:
for /f %%i in ('psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -t -c "SELECT COUNT(*) FROM users;" 2^>nul') do set USER_COUNT=%%i
for /f %%i in ('psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -t -c "SELECT COUNT(*) FROM products;" 2^>nul') do set PRODUCT_COUNT=%%i
echo   Users: %USER_COUNT%
echo   Products: %PRODUCT_COUNT%
echo.

REM Confirm before proceeding
set /p CONFIRM="Do you want to load test data? (y/n) "
if /i not "%CONFIRM%"=="y" (
    echo Seed cancelled.
    exit /b 0
)

REM Run the seed script
echo Loading test data...
set SEED_FILE=backend\src\main\resources\db\seed\seed_test_data.sql

if not exist "%SEED_FILE%" (
    echo Error: Seed file not found at %SEED_FILE%
    exit /b 1
)

psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f "%SEED_FILE%"

echo.
echo Test data loaded successfully!
echo.

REM Get updated counts
echo Updated database state:
for /f %%i in ('psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -t -c "SELECT COUNT(*) FROM users;" 2^>nul') do set USER_COUNT=%%i
for /f %%i in ('psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -t -c "SELECT COUNT(*) FROM products;" 2^>nul') do set PRODUCT_COUNT=%%i
echo   Users: %USER_COUNT%
echo   Products: %PRODUCT_COUNT%
echo.

echo ================================================
echo Test Users Created:
echo ================================================
echo Email: alice@example.com   ^| Password: password123
echo Email: bob@example.com     ^| Password: password123
echo Email: charlie@example.com ^| Password: password123
echo.
echo You can now login to the application with any of these accounts!
echo.

endlocal
