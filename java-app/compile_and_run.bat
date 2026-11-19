@echo off
REM ============================================================================
REM Build and Execution Script for Java Application
REM Waste Collection Management and Environmental Data Analysis System
REM Author: DHARANI K K (Register No.: 23)
REM ============================================================================

echo [INFO] Compiling Java Source Files...
if not exist bin mkdir bin

javac -d bin src\com\municipal\waste\model\*.java src\com\municipal\waste\util\*.java src\com\municipal\waste\dao\*.java src\com\municipal\waste\service\*.java src\com\municipal\waste\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Please ensure JDK 11+ is installed and 'javac' is in PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Compilation successful!
echo [INFO] Launching Application in Interactive Mode...
echo.
java -cp bin com.municipal.waste.Main %*
