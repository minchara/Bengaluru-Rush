@echo off
echo ============================================
echo   NEXUS DRIVE - BENGALURU RUSH
echo   Building and launching game...
echo ============================================
echo.
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-25" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-25"
    )
)
call mvnw.cmd javafx:run
pause
