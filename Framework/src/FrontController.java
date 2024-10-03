package etu2802;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private HashMap<String, Mapping> mappingUrls;

    public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
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
    
                try {
                    String packageNames = this.getInitParameter("package");
                    String[] packages = packageNames.split(",");
                    boolean foundClass = false;
    
                    for (String packageName : packages) {
                        packageName = packageName.trim();
    
                        String fullClassName = packageName + "." + map.getClassName();
                        System.out.println("full class Nale " +fullClassName);
                        try {
                            Class<?> clazz = Class.forName(fullClassName);
                            Object object = clazz.newInstance();
    
                            Utils.dispatchModelView(request, response, object, url, mappingUrls);
                            foundClass = true;
                            System.out.println("fond class "+foundClass);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
    
                    if (!foundClass) {
                        System.out.println("VOus etes ici");
                        // Utils.generateNotFoundPage(out);
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    out.println("Erreur lors de l'invocation de la méthode : " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                // Utils.generateNotFoundPage(out);
                e.printStackTrace();
                out.println(e.getMessage());
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
