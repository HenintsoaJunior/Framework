package etu2802;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Utils {
    

    public static List<Class<?>> getAllControllers(ServletContext context,String pckg) {
        List<Class<?>> controllerClasses = new ArrayList<>();
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

    public static Method getMethodByUrl(Object objet, String mappingUrlkey,HashMap<String, Mapping> mappingUrls) throws Exception {
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

    public static HashMap<String, Mapping> allMappingUrls(ServletContext context, String pckg) {
        HashMap<String, Mapping> mappingUrl = new HashMap<>();
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
            System.out.println("Classname Not found");
        }
    
        if (mappingUrl.isEmpty()) {
            throw new IllegalStateException("No URL mappings found.");
        }
    
        return mappingUrl;
    }

    public static void dispatchModelView(HttpServletRequest request, HttpServletResponse response, Object object, String mappingUrlKey,HashMap<String, Mapping> mappingUrls) throws Exception {
        PrintWriter out = response.getWriter();
        Method method = getMethodByUrl(object, mappingUrlKey,mappingUrls);
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
            out.println("Unsupport type");
            throw new IllegalArgumentException("Unsupport type");

        }
    }

    public static void generateNotFoundPage(PrintWriter out) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>404 Not Found</title>");
        out.println("<style>");
        out.println("body {font-family: Tahoma, Arial, sans-serif; color: #333; background-color: white;}");
        out.println("h1 {color: blue; text-align: center;}");
        out.println("h3 {color: #666; text-align: center;}");
        out.println(".container {width: 80%; margin: 0 auto; text-align: center;}");
        out.println(".footer {font-size: 0.8em; color: #999; text-align: center; margin-top: 20px;}");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>404 Not Found</h1>");
        out.println("<h3>The requested resource is not available.</h3>");
        out.println("</div>");
        out.println("<div class='footer'>");
        out.println("hentsa framework"); // you can customize this part as needed
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        throw new IllegalArgumentException("URL not found");
    }
    
    
}
