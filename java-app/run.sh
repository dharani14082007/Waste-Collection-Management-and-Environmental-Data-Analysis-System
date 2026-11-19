#!/bin/bash
# ============================================================================
# Build and Execution Script for Java Application (Unix / macOS)
# Author: DHARANI K K (Register No.: 23)
# ============================================================================

echo "[INFO] Compiling Java Source Files..."
mkdir -p bin

javac -d bin src/com/municipal/waste/model/*.java src/com/municipal/waste/util/*.java src/com/municipal/waste/dao/*.java src/com/municipal/waste/service/*.java src/com/municipal/waste/Main.java

if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed. Ensure JDK 11+ is installed."
    exit 1
fi

echo "[SUCCESS] Compilation successful! Launching..."
java -cp bin com.municipal.waste.Main "$@"
