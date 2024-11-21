@echo off
REM Définir les chemins des dossiers
set "src_dir=src"
set "temp_dir=temp"
set "bin_dir=bin"
set "lib=D:\Partage\Dev\github\S5\MrNaina\Framework\FrameworkCode\lib"
set "dest=D:\Partage\Dev\github\S5\MrNaina\Framework\TestFramework\lib"

REM Créer le dossier temporaire s'il n'existe pas
if not exist "%temp_dir%" (
    mkdir "%temp_dir%"
)

REM Copier tous les fichiers .java du dossier src dans le dossier temporaire sans la structure des dossiers
for /r "%src_dir%" %%f in (*.java) do (
    copy "%%f" "%temp_dir%\" /Y
)

REM Naviguer vers le dossier temp
cd "%temp_dir%"

REM Compiler tous les fichiers Java dans le dossier temp et sortir dans le dossier bin
javac -d "%bin_dir%" -cp "%lib%\*" *.java

REM Naviguer vers le dossier bin
cd "%bin_dir%"

REM Créer un fichier JAR avec les fichiers compilés
jar cvf framework.jar ./

REM Copier le fichier JAR vers un autre dossier
copy framework.jar "%dest%"

REM Copier le fichier JAR dans src/bin
copy framework.jar "%src_dir%\%bin_dir%"

REM Retourner au dossier d'origine
cd ..

REM Supprimer le dossier temporaire
rd /s /q "%temp_dir%"

pause
