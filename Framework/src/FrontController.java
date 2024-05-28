package etu2802;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private List<Class<?>> controllerClasses; // liste pour stocker les classes des contrôleurs
    private HashMap<String, Mapping> mappingUrls;

    public List<Class<?>> getControllerClasses() {
        return controllerClasses;
    }

    public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
    }

    public void setControllerClasses(List<Class<?>> controllerClasses) {
        this.controllerClasses = controllerClasses;
    }

    public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
        this.mappingUrls = mappingUrls;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String pkg = this.getInitParameter("package");
        ServletContext context = getServletContext();
        controllerClasses = Utils.getAllControllers(context,pkg);
        mappingUrls = Utils.allMappingUrls(context,pkg);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        try (PrintWriter out = response.getWriter()) {
            String url = request.getRequestURI();

            out.println("URL Requested: " + url);

            for (Class<?> controllerClass : controllerClasses) {
                AnnotationController annotation = controllerClass.getAnnotation(AnnotationController.class);
                if (annotation != null) {
                    String controllerName = controllerClass.getSimpleName();
                    out.println("Controller Found: " + controllerName);
                } else {
                    out.println("Annotation null");
                }
            }
            
            Mapping map = mappingUrls.get(url);

            if (map != null) {
                out.println("URL: " + url + " -> Controller: " + map.getClassName() + ", Method: " + map.getMethod());
                try {
                    String packageName = this.getInitParameter("package");
                    String fullClassName = packageName+"."+ map.getClassName();
                    Class<?> clazz = Class.forName(fullClassName);
                    Object object = clazz.newInstance();
                    Method method = clazz.getMethod(map.getMethod());

                    String valeur = (String) method.invoke(object);
                    out.println("La valeur retournee : " + valeur);
                
            
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    out.println("Erreur lors de l'invocation de la méthode : " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                out.println("URL not found");
            }
            

            // Afficher le contenu de mappingUrls
            // out.println("Mapping URLs sont:");
            // for (Map.Entry<String, Mapping> entry : mappingUrls.entrySet()) {
            //     String urlPattern = entry.getKey();
            //     Mapping mapping = entry.getValue();
            //     out.println("URL: " + urlPattern + " -> Controller: " + mapping.getClassName() + ", Method: " + mapping.getMethod());
            // }
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
