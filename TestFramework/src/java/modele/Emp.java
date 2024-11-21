/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modele;

import java.util.ArrayList;
import java.util.List;

import etu2802.Annotations;
import etu2802.Annotations.Restapi;
import etu2802.Annotations.URL;
import etu2802.validation.ValidationManager;
import etu2802.validation.ValidationManager.ValidationResult;
import etu2802.FileUpload;
import etu2802.ModelView;
import etu2802.MySession;

/**
 *
 * @author Henintsoa
 */ 
@Annotations.AnnotationController("Emp")
public class Emp {
    @Annotations.AnnotationAttribute("id")
    int id;
    
    @ValidationManager.Email
    @ValidationManager.MaxLength(value = 85,message = "Max valeurs depasser")
    @ValidationManager.Required(message = "Le nom est requis")
    @Annotations.AnnotationAttribute("nom")
    String nom;
    
    @ValidationManager.MaxLength(value = 5,message = "Max valeurs depasser")
    @ValidationManager.Numeric
    @Annotations.AnnotationAttribute("age")
    int age;
    
    @Annotations.AnnotationAttribute("image")
    FileUpload image;
 
    
    MySession session;
    
    public Emp() {
    }

    public Emp(int id, String nom, int age) {
        this.id = id;
        this.nom = nom;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public FileUpload getImage() {
        return image;
    }

    public void setImage(FileUpload image) {
        this.image = image;
    }

    public MySession getSession() {
        return session;
    }

    public void setSession(MySession session) {
        this.session = session;
    }
   
    @URL(lien = "/example")
    public String example(){
        return "Example";
    }
    
     @URL(lien="/AnnotationDiso")
    public ModelView AnnotationDiso(@Annotations.AnnotationParameter("id") String id,String nom,@Annotations.AnnotationParameter("age") int age){
        ModelView mv = new ModelView();
        mv.setView("test.jsp");
    
        return mv;
    }
    
    @URL(lien="/AnnotationVraie")
    public ModelView AnnotationVraie(@Annotations.AnnotationParameter("id") String id,@Annotations.AnnotationParameter("nom") String nom,@Annotations.AnnotationParameter("age") int age){
        ModelView mv = new ModelView();
        mv.setView("test.jsp");
    
        return mv;
    }
    
    @URL(lien="/lien")
    public ModelView saves(@Annotations.AnnotationParameter("employer") Emp emp){
        ModelView mv = new ModelView();
        mv.setView("saveEmp.jsp");
        mv.addItem("employer", emp);
        return mv;
    }
    
    @URL(lien="/emp")
    public ModelView emp() {
        List<Emp> emps = new ArrayList<>();
        emps.add(new Emp(1, "dax", 30));
        emps.add(new Emp(2, "alex", 25));
        emps.add(new Emp(3, "dax", 28));
        // Préparer la vue avec les employés
        ModelView mv = new ModelView();
        mv.addItem("emp", emps);  // Ajouter les employés à la vue sous l'attribut "emp"
        mv.setView("emp.jsp");  // Définir la vue à utiliser
        
        return mv;  // Retourner l'objet ModelView
    }
    
    @URL(lien="/loginPage")
    @Annotations.GET
    public ModelView loginPage(){
       ModelView mv = new ModelView();
        mv.setView("login.jsp");
    
        return mv;
    } 
    
    @URL(lien="/login")
    @Annotations.GET
    public ModelView login(){
       ModelView mv = new ModelView();
        mv.setView("login.jsp");
    
        return mv;
    } 
    
    @URL(lien="/login")
    @Annotations.POST
    @Restapi
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
    @Annotations.Restapi
    public List<Emp> getAllEmps() {
        List<Emp> emps = new ArrayList<>();
        emps.add(new Emp(1, "dax", 30));
        emps.add(new Emp(2, "alex", 25));
        emps.add(new Emp(3, "dax", 28));
        return emps;
    }
    
    @URL(lien="/logout")
    @Annotations.POST
    public ModelView logout(MySession session) {
        session.delete("users");

        ModelView mv = new ModelView();
        mv.setView("login.jsp");
        return mv;
    }

    @URL(lien = "/formulaire")
    public ModelView formulaire(MySession session) {
        ModelView mv = new ModelView();
        mv.setView("formulaire.jsp");
        return mv;
    }

    @Annotations.URL(lien="/save_employer")
    @Annotations.POST
    public ModelView save_emp(@Annotations.AnnotationParameter("employer") Emp employer) {
        ModelView mv = new ModelView();
        mv.setView("saveEmp.jsp");
        mv.addItem("employer", employer);
        
        System.out.println("ID: " + employer.getId());
        System.out.println("Nom: " + employer.getNom());
        System.out.println("Age: " + employer.getAge());
        System.out.println("Image: " + (employer.getImage() != null ? employer.getImage().getName() : "No image"));    
        return mv;
    }

}
