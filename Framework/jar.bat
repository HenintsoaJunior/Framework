cd src
set "lib=E:\jar\lib"
javac -d . -cp "%lib%\*" *.java
jar cvf framework.jar etu2802
move framework.jar C:\Users\Henintsoa\Documents\github\MrNainaS4\S4\Framework\TestFramework\lib
cd ../


pause