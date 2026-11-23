# Clinic Management

A React and Spring Boot clinic management application backed by PostgreSQL.

## Requirements

- Node.js and npm
- JDK 21 or newer
- PostgreSQL with a `clinicdb` database

## Run the backend

In PowerShell, set the PostgreSQL password and start Spring Boot:

```powershell
$env:SPRING_DATASOURCE_PASSWORD = "your-postgres-password"
Set-Location backend
.\mvnw.cmd spring-boot:run
```

The backend runs at `http://localhost:8082`.

## Run the frontend

In a second terminal:

```powershell
npm install
npm run dev
```

Open the Vite URL shown in the terminal (usually `http://localhost:5173`).
