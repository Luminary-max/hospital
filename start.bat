@echo off
set DB_PASS=041306
cd /d "%~dp0"
echo [1] Redis...
start "" "Redis-x64-5.0.9NoInstall\redis-server.exe" "Redis-x64-5.0.9NoInstall\redis.windows.conf"
echo [2] Backend...
start "Backend" cmd /c "java -jar "Hospital-idea\Hospital\target\Hospital-1.0-SNAPSHOT.jar" --spring.datasource.password=%DB_PASS%"
echo [3] Frontend...
start "Frontend" cmd /c "cd /d "HospitalVue 2\HospitalVue" && npm run serve"
echo Done. Open http://localhost:8089
pause
