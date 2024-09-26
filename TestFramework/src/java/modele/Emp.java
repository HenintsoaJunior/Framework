/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modele;

import etu2802.Annotations;
import etu2802.ModelView;
import etu2802.MySession;
import etu2802.Url;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Henintsoa
 */ 
@Annotations.AnnotationController("Emp")
public class Emp {
    @Annotations.AnnotationAttribute("id")
    int id;
    
    @Annotations.AnnotationAttribute("nom")
    String nom;
    
    @Annotations.AnnotationAttribute("age")
    int age;
    
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

    public MySession getSession() {
        return session;
    }

    public void setSession(MySession session) {
        this.session = session;
    }
    
    
   
    @Url(lien = "/example")
    public String example(){
        return "Example";
    }
    
     @Url(lien="/AnnotationDiso")
    public ModelView AnnotationDiso(@Annotations.AnnotationParameter("id") String id,String nom,@Annotations.AnnotationParameter("age") int age){
        ModelView mv = new ModelView();
        mv.setView("test.jsp");
    
        return mv;
    }
    
    @Url(lien="/AnnotationVraie")
    public ModelView AnnotationVraie(@Annotations.AnnotationParameter("id") String id,@Annotations.AnnotationParameter("nom") String nom,@Annotations.AnnotationParameter("age") int age){
        ModelView mv = new ModelView();
        mv.setView("test.jsp");
    
        return mv;
    }
    
    @Url(lien="/lien")
    public ModelView saves(@Annotations.AnnotationParameter("employer") Emp emp){
        ModelView mv = new ModelView();
        mv.setView("saveEmp.jsp");
        mv.addItem("employer", emp);
        return mv;
    }
    
    @Url(lien="/emp")
    public ModelView emp(){
        Emp[] list_emp = new Emp[4];
        list_emp[0] = new Emp(1,"Emp 1",15);
        list_emp[1] = new Emp(2,"Emp 2",16);
        list_emp[2] = new Emp(3,"Emp 3",17);
        list_emp[3] = new Emp(4,"Emp 4",18);
        

        ModelView mv = new ModelView();
        mv.setView("emp.jsp");
        mv.addItem("emp", list_emp);
        return mv;
    }
    
    @Url(lien="/loginPage")
    public ModelView loginPage(){
        ModelView mv = new ModelView();
        mv.setView("login.jsp");
    
        return mv;
    }
    
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
    
    @Url(lien="/listEmp")
    public List<Emp> getAllEmps() {
        List<Emp> emps = new ArrayList<>();
        emps.add(new Emp(1, "dax", 30));
        emps.add(new Emp(2, "alex", 25));
        emps.add(new Emp(3, "dax", 28));
        return emps;
    }

    
    @Url(lien="/logout")
    public ModelView logout(MySession session) {
        session.delete("users");

        ModelView mv = new ModelView();
        mv.setView("login.jsp");
        return mv;
    }

    
    @Url(lien = "/data")
    public ModelView getData(MySession session) {
        ModelView mv = new ModelView();
        mv.setView("dataList.jsp");
        return mv;
    }
}
