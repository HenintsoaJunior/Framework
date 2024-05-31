package etu2802;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
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
        controllerClasses = Utils.getAllControllers(context, pkg);
        mappingUrls = Utils.allMappingUrls(context, pkg);
    }

    public Method getMethodByUrl(Object objet, String mappingUrlkey) throws Exception {
        Method[] all_methods = objet.getClass().getDeclaredMethods();
        for (int i = 0; i < all_methods.length; i++) {
            Annotation[] annotations = all_methods[i].getAnnotations();
            for (int j = 0; j < annotations.length; j++) {
                if (annotations[j].annotationType() == Url.class) {
                    Url url = (Url) annotations[j];
                    if (url.lien().compareTo(mappingUrlkey) == 0 && all_methods[i].getName().compareTo(mappingUrls.get(mappingUrlkey).getMethod()) == 0) {
                        return all_methods[i];
                    }
                }
            }
        }
        throw new Exception("Method not found");
    }

    public void dispatchModelView(HttpServletRequest request, HttpServletResponse response, Object object, String mappingUrlKey) throws Exception {
        PrintWriter out = response.getWriter();
        Method method = this.getMethodByUrl(object, mappingUrlKey);
        Object returnValue = method.invoke(object);

        if (returnValue instanceof String) {
            out.println("La valeur retournée : " + returnValue);
        } else if (returnValue instanceof ModelView) {
            ModelView mv = (ModelView) returnValue;
            for (String mvKey : mv.getData().keySet()) {
                request.setAttribute(mvKey, mv.getData().get(mvKey));
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(mv.getView());
            dispatcher.forward(request, response);
        } else {
            throw new Exception("Unsupported return type");
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        try (PrintWriter out = response.getWriter()) {
            String url = request.getRequestURI();
            String contextPath = request.getContextPath();
            url = url.substring(contextPath.length());

            Mapping map = mappingUrls.get(url);

            if (map != null) {
                out.println("URL: " + url + " -> Controller: " + map.getClassName() + ", Method: " + map.getMethod());
                try {
                    String packageName = this.getInitParameter("package");
                    String fullClassName = packageName + "." + map.getClassName();
                    Class<?> clazz = Class.forName(fullClassName);
                    Object object = clazz.newInstance();
                    
                    dispatchModelView(request, response, object, url);

                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    out.println("Erreur lors de l'invocation de la méthode : " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    out.println("Erreur : " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                out.println("URL not found");
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
