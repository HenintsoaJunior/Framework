# Framework 2802

java17Tomtac8

Pour utiliser jar, vous devez annoter votre contrôleur de classe avec notre annotation : AnnotationController
Vous devez importer : etu2802.AnnotationController ;

Sprint1-2802
Vous devez saisir web xml et ajouter init-param. Voici l'exemple :
model est le nom de votre package controller

ajouter init-param
dans le param-name ajouter le nom package
dans le param-value ajouter le package de votre controller

Sprint2-2802
Pour utiliser l'annotation url, vous devez annoter la méthode

@Url(lien = "/exemple0")
    méthode public void 1() {
    }
    
Vous devez importer import etu2802.Url ;


Sprint3-2802
Pour utiliser l'annotation url, vous devez annoter la méthode
@Url(lien = "/Framework/example1")
    public String methode2() {
        return "Url methode1";
    }

configuration init param

init-param
    param-name>package/param-name>
    param-value>modele/param-value>
    description>package_modele/description
init-param


met dans le navigateur le nom de vore url par example :example1
et le but du sprint3 c'est de d'ajouter une type de return string
        