@echo off
REM ============================================================================
REM One-Click GitHub Push Script for Dharani K K (dharani14082007)
REM ============================================================================

echo ==========================================================================
echo   Pushing Waste Collection Management System to GitHub
echo   Account : dharani14082007 (dharani14082007@gmail.com)
echo   Repo    : Municipal-Waste-Collection-Management-System
echo ==========================================================================
echo.

set GIT_EXE="C:\Users\ASUS\AppData\Local\GitHubDesktop\app-3.6.4\resources\app\git\cmd\git.exe"
if not exist %GIT_EXE% set GIT_EXE=git

echo [INFO] Current Git Remote:
%GIT_EXE% remote -v
echo.

echo [INFO] Pushing branch 'main' to GitHub...
echo (If prompted, sign in via browser or enter your GitHub Personal Access Token)
echo.

%GIT_EXE% push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==========================================================================
    echo   [SUCCESS] Project successfully pushed to:
    echo   https://github.com/dharani14082007/Municipal-Waste-Collection-Management-System
    echo ==========================================================================
) else (
    echo.
    echo [ERROR] Push requires authentication. Please check your GitHub credentials or use GitHub Desktop.
)

pause
