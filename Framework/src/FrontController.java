package etu2802;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
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
        try {
            String pkg = this.getInitParameter("package");
            ServletContext context = getServletContext();
            controllerClasses = Utils.getAllControllers(context, pkg);
            mappingUrls = Utils.allMappingUrls(context, pkg);   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            String url = request.getRequestURI();
            String contextPath = request.getContextPath();
            url = url.substring(contextPath.length());
    
            try {
                Mapping map = mappingUrls.get(url);
    
                out.println("URL: " + url + " -> Controller: " + map.getClassName() + ", Method: " + map.getMethod());
                try {
                    String packageName = this.getInitParameter("package");
                    String fullClassName = packageName + "." + map.getClassName();
                    Class<?> clazz = Class.forName(fullClassName);
                    Object object = clazz.newInstance();
                    
                    Utils.dispatchModelView(request, response, object, url, mappingUrls);
    
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    out.println("Erreur lors de l'invocation de la méthode : " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Utils.generateNotFoundPage(out);
                e.printStackTrace();
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
