# Framework 2802

## Prérequis
- Java 17
- Tomcat 8

## Utilisation

### Sprint 1 - Configuration Initiale

Pour utiliser le fichier JAR, vous devez annoter votre classe contrôleur avec notre annotation `@AnnotationController`.

```java
import etu2802.AnnotationController;
```

#### Configuration du `web.xml`
Vous devez saisir le fichier `web.xml` et ajouter un `init-param`. Voici un exemple :

```xml
<init-param>
    <param-name>package</param-name>
    <param-value>votre.package.controller</param-value>
</init-param>
```

- **param-name** : ajoutez le nom du package.
- **param-value** : ajoutez le package de votre contrôleur.

### Sprint 2 - Annotation d'URL

Pour utiliser l'annotation `@Url`, vous devez annoter la méthode souhaitée.

```java
import etu2802.Url;

@Url(lien = "/exemple0")
public void methode1() {
    // votre code ici
}
```

### Sprint 3 - Exemple Avancé d'Annotation d'URL

Pour utiliser l'annotation `@Url`, vous devez annoter la méthode comme suit :

```java
import etu2802.Url;

@Url(lien = "/Framework/example1")
public String methode2() {
    return "Url methode1";
}
```

#### Configuration du `web.xml`

```xml
<init-param>
    <param-name>package</param-name>
    <param-value>modele</param-value>
    <description>package_modele</description>
</init-param>
```

Dans votre navigateur, saisissez l'URL comme suit : `/example1`.

Le but de ce sprint est d'ajouter un type de retour `String`.

### Sprint 4 - Utilisation de Dispatcher

Le retour de la fonction doit être `ModelView`. Voici un exemple de code :

```java
import etu2802.Url;

@Url(lien="/emp")
public ModelView emp() {
    Emp[] listEmp = new Emp[3];
    listEmp[0] = new Emp("Emp 1");
    listEmp[1] = new Emp("Emp 2");
    listEmp[2] = new Emp("Emp 3");

    ModelView mv = new ModelView();
    mv.setView("emp.jsp");
    mv.addItem("emp", listEmp);
    return mv;
}
```

Pour récupérer les valeurs dans `emp.jsp` :

```java
Emp[] listEmp = (Emp[])request.getAttribute("emp");
```

On utilise `getAttribute()` pour récupérer les valeurs.

Il est également nécessaire d'ajouter un constructeur vide à chaque classe :

```java
public Emp() {
}
```

---

### Sprint 5 - Gestion D'exeption

-package not found
-page not found
-Duplicata url
-Return type


En suivant ces étapes, vous pourrez configurer et utiliser le Framework 2802 efficacement
