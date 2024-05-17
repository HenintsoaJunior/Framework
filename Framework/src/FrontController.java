package etu2802;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.lang.reflect.Method;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class FrontController extends HttpServlet {
    private List<Class<?>> controllerClasses; //liste pour stocker les classes des contrôleurs

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String pkg = this.getInitParameter("package");
        controllerClasses = allMappingUrls(pkg);
    }

    public List<Class<?>> allMappingUrls(String pckg) {
        controllerClasses = new ArrayList<>(); 

        ServletContext context = getServletContext(); // Obtention du contexte du servlet
        String path = "/WEB-INF/classes/" + pckg;

        Set<String> classNames = context.getResourcePaths(path);// Récupération des chemins des ressources dans le package
        if (classNames != null) {
            for (String className : classNames) {
                if (className.endsWith(".class")) {
                    String fullClassName = className.substring(0, className.length() - 6); // Suppression de l'extension ".class"
                    int taille = fullClassName.split("/").length;
                    fullClassName = fullClassName.split("/")[taille - 2] + "." + fullClassName.split("/")[taille - 1]; // Construction du nom complet de la classe 
                    try {
                        Class<?> myClass = Class.forName(fullClassName);
                        AnnotationController annotation = myClass.getAnnotation(AnnotationController.class); // Récupération de l'annotation
                        if (annotation != null) {
                            System.out.println("anotation value"+annotation.value());
                            controllerClasses.add(myClass); // Ajout de la classe à la liste
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }
        else{
            System.out.println("Class Name null");
        }
        return controllerClasses;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        try (PrintWriter out = response.getWriter()) {
            String url = request.getRequestURI();
            out.println("URL Requested: " + url);
            
            out.println(controllerClasses);
            for (Class<?> controllerClass : controllerClasses) {
                AnnotationController annotation = controllerClass.getAnnotation(AnnotationController.class);
                if (annotation != null) {
                    String controllerName = controllerClass.getSimpleName();
                    out.println("Controller Found: " + controllerName);
                } else {
                    out.println("Annotation null");
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
