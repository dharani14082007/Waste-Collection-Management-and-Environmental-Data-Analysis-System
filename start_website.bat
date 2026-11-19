@echo off
REM ============================================================================
REM One-Click Launcher for Municipal Waste Management Web Portal
REM Author: DHARANI K K (Register No.: 23)
REM ============================================================================

echo ==========================================================================
echo   Launching Municipal Waste Management & Environmental Analytics Portal
echo   Developer: DHARANI K K (Register No.: 23)
echo ==========================================================================
echo.
echo Starting Python Flask Server on http://localhost:5000 ...
echo Opening web portal in your default browser...

start http://localhost:5000
python web-app\app.py
pause
