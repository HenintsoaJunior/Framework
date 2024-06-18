package etu2802;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Utils {
    


    public static void checkPackageExists(String packageName, ServletContext context) {
        String packagePath = "/WEB-INF/classes/" + packageName.replace('.', '/');
        Set<String> resourcePaths = context.getResourcePaths(packagePath);
        if (resourcePaths == null || resourcePaths.isEmpty()) {
            throw new IllegalArgumentException("Le package " + packageName + " n'existe pas ou n'est pas configuré dans init-param.");
        }
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


    public static HashMap<String, Mapping> allMappingUrls(ServletContext context, String packageNames) {
        HashMap<String, Mapping> mappingUrl = new HashMap<>();
        
        String[] packages = packageNames.split(",");
        
        for (String packageName : packages) {
            packageName = packageName.trim();
            checkPackageExists(packageName, context);
    
            String path = "/WEB-INF/classes/" + packageName.replace('.', '/');
    
            Set<String> classNames = context.getResourcePaths(path);
            if (classNames != null) {
                for (String className : classNames) {
                    if (className.endsWith(".class")) {
                        String fullClassName = packageName + "." + className.substring(path.length() + 1, className.length() - 6).replace('/', '.');
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
        }
    
        if (mappingUrl.isEmpty()) {
            throw new IllegalStateException("No URL mappings found.");
        }
    
        return mappingUrl;
    }
    
    
    public static void dispatchModelView(HttpServletRequest request, HttpServletResponse response, Object object, String mappingUrlKey, HashMap<String, Mapping> mappingUrls) throws Exception {
        PrintWriter out = response.getWriter();
        try {
            Method method = getMethodByUrl(object, mappingUrlKey, mappingUrls);
            Object[] obj = getMethodParams(method, request);
            Object returnValue = method.invoke(object, obj);
    
            if (returnValue instanceof String) {
                out.println("La valeur retournée : " + returnValue);
            } else if (returnValue instanceof ModelView) {
                ModelView mv = (ModelView) returnValue;
                for (String mvKey : mv.getData().keySet()) {
                    request.setAttribute(mvKey, mv.getData().get(mvKey));
                    out.println("id" + request.getParameter("id"));
                    out.println("nom" + request.getParameter("nom"));
                    out.println("age" + request.getParameter("age"));
                }
    
                RequestDispatcher dispatcher = request.getRequestDispatcher(mv.getView());
                dispatcher.forward(request, response);
            } else {
                out.println("Unsupport type");
                throw new IllegalArgumentException("Unsupport type");
            }
        } catch (Exception e) {
            // Gérer l'exception ici
            out.println("Une erreur s'est produite : " + e.getMessage());
            e.printStackTrace(); // À des fins de débogage, peut être supprimé en production
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
    
    public static Object[] getMethodParams(Method method, HttpServletRequest request) throws IllegalArgumentException {
        Parameter[] parameters = method.getParameters();
        Object[] methodParams = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String paramName = "";
            if (parameters[i].isAnnotationPresent(Annotations.AnnotationParameter.class)) {
                paramName = parameters[i].getAnnotation(Annotations.AnnotationParameter.class).value();
            } else {
                paramName = parameters[i].getName();
            }

            Class<?> paramType = parameters[i].getType();

            // Si le type du paramètre est un objet complexe (non primitif et non String)
            if (!paramType.isPrimitive() && !paramType.equals(String.class)) {
                try {
                    Object paramObject = paramType.getDeclaredConstructor().newInstance();
                    Field[] fields = paramType.getDeclaredFields();
                    
                    for (Field field : fields) {
                        String fieldName = field.getName();
                        String fieldValue = request.getParameter(paramName + "." + fieldName);
                        if (fieldValue != null) {
                            field.setAccessible(true);
                            Object typedValue = typage(fieldValue, fieldName, field.getType());
                            field.set(paramObject, typedValue);
                        }
                    }
                    methodParams[i] = paramObject;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new IllegalArgumentException("Error creating parameter object: " + paramName, e);
                }
            } else {
                String paramValue = request.getParameter(paramName);
                if (paramValue == null) {
                    throw new IllegalArgumentException("Missing parameter: " + paramName);
                }
                methodParams[i] = typage(paramValue, paramName, paramType);
            }
        }
        return methodParams;
    }

    public static Object typage(String paramValue ,String paramName, Class paramType){
        Object o = null ;
        if (paramType == Date.class || paramType == java.sql.Date.class) {
            try {
                o = java.sql.Date.valueOf(paramValue);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid date format for parameter: " + paramName);
            }
        } else if (paramType == int.class) {
            o = Integer.parseInt(paramValue);
        } else if (paramType == double.class) {
            o = Double.parseDouble(paramValue);
        } else if (paramType == boolean.class) {
            o =Boolean.parseBoolean(paramValue);
        } else {
            o = paramValue; 
        }
        return o;
    }
}
