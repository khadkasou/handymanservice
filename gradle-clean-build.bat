@echo off
REM Fix for: DefaultCacheCoordinator... is null / FileLock.writeFile
REM Run from repo root. Defaults to Handyman-Service.
REM Pass a service folder name to build only that service, e.g. gradle-clean-build.bat Api-Gateway
REM Requires Java 25 (toolchain languageVersion = 25).

if not defined JAVA_HOME (
  if exist "C:\Program Files\Java\jdk-25.0.4" set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.4"
)
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Using JAVA_HOME=%JAVA_HOME%
java -version

set SVC=%~1
if "%SVC%"=="" set SVC=Handyman-Service

echo Stopping Gradle daemon...
call "%SVC%\gradlew.bat" --stop 2>nul
if exist "%USERPROFILE%\.gradle\daemon" (
  echo Removing Gradle daemon dir...
  rmdir /s /q "%USERPROFILE%\.gradle\daemon" 2>nul
)
if exist "%USERPROFILE%\.gradle\caches" (
  echo Removing Gradle caches (will re-download)...
  rmdir /s /q "%USERPROFILE%\.gradle\caches" 2>nul
)

echo Building %SVC% with --no-daemon...
cd /d "%~dp0\%SVC%"
call gradlew.bat --no-daemon clean build
set EX=%errorlevel%
cd /d "%~dp0"
exit /b %EX%
