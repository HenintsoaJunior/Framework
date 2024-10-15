cd src/etu2802

:: Chemin vers le dossier contenant les JAR
set "lib=C:\Users\Henintsoa\Documents\github\S5\MrNaina\Framework\FrameworkCode\lib"

:: Compilation des fichiers Java avec les dépendances JAR
javac -d . -cp "%lib%\*;." *.java

:: Création du fichier JAR
jar cvf framework.jar etu2802

:: Déplacement du JAR dans le dossier lib de TestFramework
move framework.jar C:\Users\Henintsoa\Documents\github\S5\MrNaina\Framework\TestFramework\lib

:: Retour au répertoire parent
cd ../

pause
