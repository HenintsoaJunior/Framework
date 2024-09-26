@echo off
for /F "tokens=*" %%A in (config.conf) do set "%%A"
set "sourceFolder=%root%\src\java"

for /r "%sourceFolder%" %%f in (*.java) do (
    xcopy "%%f" "%root%\temp"
)

set "destinationFolder=%root%\bin"
set "lib=%root%\lib"

cd "%root%\temp"
javac -d "%destinationFolder%" -cp "%lib%\*" *.java

pause
