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

### Spring 6 Get Valeurs des Formulaire

Ajouter des annotation pour les parametre a recuperer 

```java
@Url(lien="/save_employe")
    public ModelView save(@Annotations.AnnotationParameter("id") int id,@Annotations.AnnotationParameter("nom") String nom,@Annotations.AnnotationParameter("age") int age){
        ModelView mv = new ModelView();
        mv.setView("saveEmp.jsp");

        return mv;
    }
```

les name des input doit etre comme celle des parametre annoter

```html
<form action="save_employer" method="post">
  <input type="number" name="id">
  <input type="text" name="nom">
  <input type="number" name="age">
  <input type="submit" value="Valider">
 </form>
```

pour recuperer les valeurs

```jsp
<%
    <%= request.getParameter("id") %>
    <%= request.getParameter("nom") %>
    <%= request.getParameter("age") %>
%>
```

### Spring 7 Get Valeurs des Formulaire Avec Objet

Mettre directement l'objet sur le parametre

```java
@Url(lien="/saves_employer")
    public ModelView saves(@Annotations.AnnotationParameter("employer") Emp emp){
        ModelView mv = new ModelView();
        mv.setView("saveEmp.jsp");
        mv.addItem("employer", emp);
        return mv;
    }
```

les name des input doit etre comme l'exemple suivante ajouter le nom de l'objet dans l'annotation avant le name et ajouter de
. puis les attribut

```html
<form action="saves_employer" method="post">
  Id : <input type="number" name="employer.id">
  Nom : <input type="text" name="employer.nom">
  age : <input type="number" name="employer.age">
  <input type="submit" value="Valider">
 </form>
```

pour recuperer les valeurs

```jsp
<%
    Emp emp = (Emp) request.getAttribute("employer");
%>
```

```jsp
<%
    <%= emp.getId() %>
    <%= emp.getNom() %>
    <%= emp.getAge() %>
%>
```

### Spring 8 session

Pour utiliser le session il y deux facon

Premierement creation d'une session au niveau attribut

```java
MySession session
```

example de code pour faire une autentification et logout

```java
@Url(lien="/login")
    public ModelView login(@Annotations.AnnotationParameter("identifiant") String identifiant, @Annotations.AnnotationParameter("motdepasse") String motDePasse) {
        List<Emp> allEmps = getAllEmps();

        List<Emp> matchedEmps = new ArrayList<>();
        for (Emp emp : allEmps) {
            if (emp.getNom().equals(identifiant) && motDePasse.equals("123")) {
                matchedEmps.add(emp);
            }
        }

        if (!matchedEmps.isEmpty()) {
            this.session.add("users", matchedEmps);
            ModelView mv = new ModelView();
            mv.setView("dataList.jsp");
            return mv;
        } else {
            ModelView mv = new ModelView();
            mv.setView("login.jsp");
            mv.addItem("error", "Identifiant ou mot de passe incorrect");
            return mv;
        }
    }


    @Url(lien="/logout")
    public ModelView logout() {
        this.session.delete("users");

        ModelView mv = new ModelView();
        mv.setView("login.jsp");
        return mv;
    }
```

deuxiement creation d'une session au niveau methode

```java
@Url(lien="/login")
    public ModelView login(@Annotations.AnnotationParameter("identifiant") String identifiant, @Annotations.AnnotationParameter("motdepasse") String motDePasse,MySession session) {
        List<Emp> allEmps = getAllEmps();

        List<Emp> matchedEmps = new ArrayList<>();
        for (Emp emp : allEmps) {
            if (emp.getNom().equals(identifiant) && motDePasse.equals("123")) {
                matchedEmps.add(emp);
            }
        }

        if (!matchedEmps.isEmpty()) {
            session.add("users", matchedEmps);
            ModelView mv = new ModelView();
            mv.setView("dataList.jsp");
            return mv;
        } else {
            ModelView mv = new ModelView();
            mv.setView("login.jsp");
            mv.addItem("error", "Identifiant ou mot de passe incorrect");
            return mv;
        }
    }


    @Url(lien="/logout")
    public ModelView logout(MySession session) {
        session.delete("users");

        ModelView mv = new ModelView();
        mv.setView("login.jsp");
        return mv;
    }
```

### Spring 9 Reponse JSON

Si vous voulez returner une reponse JSON il faut annoter votre Methode en REST API

```java
@Restapi
```

voici une example de code

```java
    @GET(lien="/listEmp")
    @Annotations.Restapi
    public List<Emp> getAllEmps() {
        List<Emp> emps = new ArrayList<>();
        emps.add(new Emp(1, "dax", 30));
        emps.add(new Emp(2, "alex", 25));
        emps.add(new Emp(3, "dax", 28));
        return emps;
    }

```

### Spring 10 Utilisation de POST et GET

```java
    @URL(lien="/login")
    @Annotations.POST
```

```java
    @URL(lien="/listEmp")
    @Annotations.GET
```

Voici une example Concret

```java
    @URL(lien="/login")
    @Annotations.POST
    public ModelView login(@Annotations.AnnotationParameter("identifiant") String identifiant, @Annotations.AnnotationParameter("motdepasse") String motDePasse) {
        List<Emp> allEmps = getAllEmps();

        List<Emp> matchedEmps = new ArrayList<>();
        for (Emp emp : allEmps) {
            if (emp.getNom().equals(identifiant) && motDePasse.equals("123")) {
                matchedEmps.add(emp);
            }
        }

        if (!matchedEmps.isEmpty()) {
            this.session.add("users", matchedEmps);
            ModelView mv = new ModelView();
            mv.setView("dataList.jsp");
            return mv;
        } else {
            ModelView mv = new ModelView();
            mv.setView("login.jsp");
            mv.addItem("error", "Identifiant ou mot de passe incorrect");
            return mv;
        }
    }

    @URL(lien="/listEmp")
    @Annotations.GET
    @Annotations.Restapi
    public List<Emp> getAllEmps() {
        List<Emp> emps = new ArrayList<>();
        emps.add(new Emp(1, "dax", 30));
        emps.add(new Emp(2, "alex", 25));
        emps.add(new Emp(3, "dax", 28));
        return emps;
    }
    
```

### Spring 11 Gestion d'exeption de POST et GET

GESTION D'erreur

En suivant ces étapes, vous pourrez configurer et utiliser le Framework 2802 efficacement

### Spring 12 Validation des valeurs

File Upload 
upload fichier
En suivant ces étapes, vous pourrez configurer et utiliser le Framework 2802 efficacement

### Sprint 13 Throws Validation

Ajoute des annotations des Validation
checker les validation et returner une exception si le validation n'est pas respecter

```java
@ValidationManager.Email
    @ValidationManager.MaxLength(value = 85,message = "Max valeurs depasser")
    @ValidationManager.Required(message = "Le nom est requis")
    @Annotations.AnnotationAttribute("nom")
    String nom;
    
    @ValidationManager.MaxLength(value = 5,message = "Max valeurs depasser")
    @ValidationManager.Numeric
    @Annotations.AnnotationAttribute("age")
    int age;
    
```

### Sprint 14 Retour des erreurs de validation sur le formulaire

Ajoute des annotations des Validation
checker les validation et returner une exception si le validation n'est pas respecter

```java
@ValidationManager.Email
    @ValidationManager.MaxLength(value = 85,message = "Max valeurs depasser")
    @ValidationManager.Required(message = "Le nom est requis")
    @Annotations.AnnotationAttribute("nom")
    String nom;
    
    @ValidationManager.MaxLength(value = 5,message = "Max valeurs depasser")
    @ValidationManager.Numeric
    @Annotations.AnnotationAttribute("age")
    int age;
    
```

```jsp
<%
    Map<String, List<String>> errors = (Map<String, List<String>>) session.getAttribute("errors");
    Map<String, String> validFormData = (Map<String, String>) session.getAttribute("validFormData");

    if (errors == null) errors = new HashMap<>();
    if (validFormData == null) validFormData = new HashMap<>();
%>

<form action="save_employer" method="post" enctype="multipart/form-data">
        <div class="form-group <%= FormErrorHandler.getErrorClass(errors, "id") %>">
        <label for="id">Id :</label>
            <input type="text" id="id" name="employer.id" 
                value="<%= FormErrorHandler.getValueOrDefault(validFormData, "employer.id", "") %>">
            <%= FormErrorHandler.renderErrors(errors, "id") %>
        </div>

        <div class="form-group <%= FormErrorHandler.getErrorClass(errors, "nom") %>">
            <label for="nom">Nom :</label>
            <input type="text" id="nom" name="employer.nom" 
                value="<%= FormErrorHandler.getValueOrDefault(validFormData, "employer.nom", "") %>">
            <%= FormErrorHandler.renderErrors(errors, "nom") %>
        </div>

        <div class="form-group <%= FormErrorHandler.getErrorClass(errors, "age") %>">
            <label for="age">Age :</label>
            <input type="number" id="age" name="employer.age" 
                value="<%= FormErrorHandler.getValueOrDefault(validFormData, "employer.age", "") %>">
            <%= FormErrorHandler.renderErrors(errors, "age") %>
        </div>

        <div class="form-group <%= FormErrorHandler.getErrorClass(errors, "image") %>">
            <label for="image">Image :</label>
            <input type="file" id="image" name="employer.image" accept="image/*">
            <%= FormErrorHandler.renderErrors(errors, "image") %>
        </div>

        <input type="submit" value="Valider" class="submit-button">
    </form>
```
