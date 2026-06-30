@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo   Complaint Management System Build ^& Run Helper
echo ===================================================

:: Use the installed JDK path
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot
set JAVAC="%JAVA_HOME%\bin\javac.exe"
set JAVA="%JAVA_HOME%\bin\java.exe"

:: Setup directories
if not exist lib mkdir lib
if not exist bin mkdir bin

:: Download SQLite JDBC driver if not present
set JDBC_JAR=lib\sqlite-jdbc-3.47.1.0.jar

if not exist %JDBC_JAR% (
    echo SQLite JDBC driver not found. Please place sqlite-jdbc-3.47.1.0.jar in the lib folder.
    pause
    exit /b 1
) else (
    echo SQLite JDBC driver found in lib.
)

:: Compile all Java source files
echo Compiling source files...
%JAVAC% -d bin -cp "lib\*" src\model\*.java src\exception\*.java src\repository\*.java src\service\*.java src\ui\*.java src\App.java

if !errorlevel! neq 0 (
    echo Compilation failed. Please check errors above.
    pause
    exit /b 1
)
echo Compilation successful!

:: Run the application
echo Launching Complaint Management System...
%JAVA% -cp "bin;lib\*" App

endlocal
