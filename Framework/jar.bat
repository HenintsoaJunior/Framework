cd src
set "lib=E:\jar\lib"
javac -d . -cp "%lib%\*" *.java
jar cvf framework.jar etu2802
move framework.jar C:\Users\Henintsoa\Documents\github\Framework\sprint0-2802\TestFramework\lib
cd ../


pause