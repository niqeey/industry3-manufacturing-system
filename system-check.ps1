#!/bin/bash
# system-check.ps1 - Comprehensive system diagnostic script

Write-Host "=== Industry 3.0 System Diagnostic ===" -ForegroundColor Green

# Check Java
Write-Host "`n1. Checking Java..." -ForegroundColor Yellow
try {
    java -version
    Write-Host "✅ Java is installed" -ForegroundColor Green
} catch {
    Write-Host "❌ Java not found or not in PATH" -ForegroundColor Red
}

# Check Node.js
Write-Host "`n2. Checking Node.js..." -ForegroundColor Yellow
try {
    node --version
    npm --version
    Write-Host "✅ Node.js and NPM are installed" -ForegroundColor Green
} catch {
    Write-Host "❌ Node.js not found or not in PATH" -ForegroundColor Red
}

# Check MySQL
Write-Host "`n3. Checking MySQL..." -ForegroundColor Yellow
try {
    mysql --version
    Write-Host "✅ MySQL is installed" -ForegroundColor Green
} catch {
    Write-Host "❌ MySQL not found or not in PATH" -ForegroundColor Red
}

# Check Ports
Write-Host "`n4. Checking Port Usage..." -ForegroundColor Yellow
$port8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
$port3000 = Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue

if ($port8080) {
    Write-Host "✅ Port 8080 is in use (Backend likely running)" -ForegroundColor Green
} else {
    Write-Host "⚠️ Port 8080 is free (Backend may not be running)" -ForegroundColor Yellow
}

if ($port3000) {
    Write-Host "✅ Port 3000 is in use (Frontend likely running)" -ForegroundColor Green
} else {
    Write-Host "⚠️ Port 3000 is free (Frontend may not be running)" -ForegroundColor Yellow
}

# Test Backend API
Write-Host "`n5. Testing Backend API..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/products" -Method GET -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Backend API is responding" -ForegroundColor Green
        $jsonData = $response.Content | ConvertFrom-Json
        Write-Host "📊 Found $($jsonData.Count) products in database" -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ Backend API not responding: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Frontend
Write-Host "`n6. Testing Frontend..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000" -Method GET -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Frontend is responding" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Frontend not responding: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Database Connection
Write-Host "`n7. Testing Database..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -Method GET -TimeoutSec 5
    $health = $response.Content | ConvertFrom-Json
    if ($health.status -eq "UP") {
        Write-Host "✅ Application health check passed" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Health check failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Check for common error files
Write-Host "`n8. Checking for Error Logs..." -ForegroundColor Yellow
$backendTarget = "C:\IslandPlusSolution\IndustryIII\backend\target"
$frontendNodeModules = "C:\IslandPlusSolution\IndustryIII\frontend\node_modules"

if (Test-Path $backendTarget) {
    Write-Host "✅ Backend compiled successfully (target folder exists)" -ForegroundColor Green
} else {
    Write-Host "⚠️ Backend target folder not found - may need compilation" -ForegroundColor Yellow
}

if (Test-Path $frontendNodeModules) {
    Write-Host "✅ Frontend dependencies installed" -ForegroundColor Green
} else {
    Write-Host "❌ Frontend node_modules missing - need to run 'npm install'" -ForegroundColor Red
}

Write-Host "`n=== Diagnostic Complete ===" -ForegroundColor Green
Write-Host "`nRecommended Actions:" -ForegroundColor Cyan
Write-Host "- If backend API failed: Check MySQL service and database credentials"
Write-Host "- If frontend failed: Run 'npm install' and 'npm start' in frontend directory"
Write-Host "- If ports are busy: Check for existing processes and restart if needed"
Write-Host "- Check TESTING.md and TROUBLESHOOTING.md for detailed solutions"