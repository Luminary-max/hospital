@echo off
set DB_PASS=041306
cd /d "%~dp0"
echo [1] Redis...
start "" "Redis-x64-5.0.9NoInstall\redis-server.exe" "Redis-x64-5.0.9NoInstall\redis.windows.conf"
echo [2] Backend...
start "Backend" cmd /c "java -jar "Hospital-idea\Hospital\target\Hospital-1.0-SNAPSHOT.jar" --spring.datasource.password=%DB_PASS%"
echo [3] Frontend (compiling... please wait ~30 seconds)...
start "Frontend" cmd /c "cd /d "HospitalVue 2\HospitalVue" && npm run serve"
echo Waiting 30 seconds for frontend compilation...
timeout /t 30 /nobreak >nul
echo Done. Open http://localhost:8089 in Chrome/Edge
echo If page is blank, wait a few more seconds and refresh (F5)
pause
