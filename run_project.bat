@echo off
REM ============================================================================
REM Master Control Launcher for Municipal Waste Management System
REM Author: DHARANI K K (Register No.: 23)
REM Velalar College of Engineering and Technology, Erode - 12
REM ============================================================================

:MENU
cls
echo ==========================================================================
echo   VELALAR COLLEGE OF ENGINEERING AND TECHNOLOGY, ERODE - 12 (AUTONOMOUS)
echo                 DEPARTMENT OF INFORMATION TECHNOLOGY
echo    Waste Collection Management and Environmental Data Analysis System
echo       Developer: DHARANI K K  |  Reg No: 23  |  Class: 25IT3A^&B
echo ==========================================================================
echo.
echo Please select what you want to run:
echo.
echo   [1] Launch Interactive Web Portal Dashboard (Flask + Browser)
echo   [2] Open Direct Offline Website in Browser (No Server Needed)
echo   [3] Compile and Run Java Console Application (Interactive Menu)
echo   [4] Run Java Automated Showcase Demo (--demo)
echo   [5] Run Python Environmental Data Science Analysis (Generate 6 Charts)
echo   [6] Install Python Requirements (pip install -r requirements.txt)
echo   [7] Exit
echo.
set /p choice="Enter choice (1-7): "

if "%choice%"=="1" goto WEB_SERVER
if "%choice%"=="2" goto WEB_OFFLINE
if "%choice%"=="3" goto JAVA_APP
if "%choice%"=="4" goto JAVA_DEMO
if "%choice%"=="5" goto PYTHON_EDA
if "%choice%"=="6" goto INSTALL_REQ
if "%choice%"=="7" exit /b 0

echo Invalid choice. Please enter a number between 1 and 7.
pause
goto MENU

:WEB_SERVER
echo.
echo [INFO] Starting Flask Server and opening http://localhost:5000 ...
start http://localhost:5000
python web-app\app.py
pause
goto MENU

:WEB_OFFLINE
echo.
echo [INFO] Opening direct offline website in your default browser...
start website\index.html
goto MENU

:JAVA_APP
echo.
echo [INFO] Launching Java Application...
cd java-app
call compile_and_run.bat
cd ..
pause
goto MENU

:JAVA_DEMO
echo.
echo [INFO] Launching Java Application in Automated Demo Mode...
cd java-app
call compile_and_run.bat --demo
cd ..
pause
goto MENU

:PYTHON_EDA
echo.
echo [INFO] Running Python Data Science Pipeline & Generating Charts...
python python-analysis\waste_analysis.py
echo.
echo [SUCCESS] Charts generated in: python-analysis\visualizations\
pause
goto MENU

:INSTALL_REQ
echo.
echo [INFO] Installing Python dependencies from requirements.txt...
pip install -r requirements.txt
echo.
echo [SUCCESS] Dependencies installed!
pause
goto MENU
