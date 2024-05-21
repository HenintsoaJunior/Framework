package etu2802;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        controllerClasses = getAllControllers(pkg);
        mappingUrls = allMappingUrls(pkg);
    }

    public List<Class<?>> getAllControllers(String pckg) {
        List<Class<?>> controllerClasses = new ArrayList<>();
        ServletContext context = getServletContext();
        String path = "/WEB-INF/classes/" + pckg.replace('.', '/');
        
        Set<String> classNames = context.getResourcePaths(path);
        if (classNames != null) {
            for (String className : classNames) {
                if (className.endsWith(".class")) {
                    String fullClassName = className.substring("/WEB-INF/classes/".length(), className.length() - 6).replace('/', '.');
                    try {
                        Class<?> myClass = Class.forName(fullClassName);
                        if (myClass.isAnnotationPresent(AnnotationController.class)) {
                            controllerClasses.add(myClass);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Class Name null");
        }
        return controllerClasses;
    }

    public HashMap<String, Mapping> allMappingUrls(String pckg) {
        HashMap<String, Mapping> mappingUrl = new HashMap<>();
        ServletContext context = getServletContext();
        String path = "/WEB-INF/classes/" + pckg.replace('.', '/');

        Set<String> classNames = context.getResourcePaths(path);
        if (classNames != null) {
            for (String className : classNames) {
                if (className.endsWith(".class")) {
                    String fullClassName = className.substring("/WEB-INF/classes/".length(), className.length() - 6).replace('/', '.');
                    try {
                        Class<?> myClass = Class.forName(fullClassName);
                        for (Method method : myClass.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(Url.class)) {
                                Url url = method.getAnnotation(Url.class);
                                Mapping map = new Mapping(myClass.getSimpleName(), method.getName());
                                if (mappingUrl.containsKey(url.lien())) {
                                    throw new IllegalArgumentException("Duplicate URL mapping found: " + url.lien());
                                }
                                mappingUrl.put(url.lien(), map);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Class Name null");
        }
        return mappingUrl;
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
                    out.println("Controller Found Sont: " + controllerName);
                } else {
                    out.println("Annotation null");
                }
            }

            // Afficher le contenu de mappingUrls
            out.println("Mapping URLs sont:");
            for (Map.Entry<String, Mapping> entry : mappingUrls.entrySet()) {
                String urlPattern = entry.getKey();
                Mapping mapping = entry.getValue();
                out.println("URL: " + urlPattern + " -> Controller: " + mapping.getClassName() + ", Method: " + mapping.getMethod());
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
