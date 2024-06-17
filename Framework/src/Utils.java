package etu2802;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
                out.println("id" +request.getParameter("id"));
                out.println("nom" +request.getParameter("nom"));
                out.println("age" +request.getParameter("age"));
            
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(mv.getView());
            dispatcher.forward(request, response);
        } else {
            out.println("Unsupport type");
            throw new IllegalArgumentException("Unsupport type");

        }
    }


    public static void setObject(HttpServletRequest request, HttpServletResponse response, Object objet) throws Exception {
        Field[] attributs = objet.getClass().getDeclaredFields();

        for (Field field : attributs) {
            if (field.isAnnotationPresent(AnnotationAttribute.class)) {
                AnnotationAttribute annotation = field.getAnnotation(AnnotationAttribute.class);
                String fieldName = annotation.value();
        
                String[] parameter = request.getParameterValues(fieldName);
                if (parameter != null && parameter.length > 0) {
                    System.out.println("Contenu du tableau pour le champ " + fieldName + ":");
                    for (String value : parameter) {
                        System.out.println(value);
                    }
        
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    if (fieldType.equals(int.class)) {
                        field.setInt(objet, Integer.parseInt(parameter[0]));
                    } else if (fieldType.equals(String.class)) {
                        field.set(objet, parameter[0]);
                    } else if (fieldType.equals(double.class)) {
                        field.setDouble(objet, Double.parseDouble(parameter[0]));
                    }
                }
            }
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
    

        // public static void setObject(HttpServletRequest request, HttpServletResponse response, Object objet) throws Exception {
    //     Field[] attributs = objet.getClass().getDeclaredFields();
    //     String[] setters = new String[attributs.length];
    
    //     for (int i = 0; i < attributs.length; i++) {
    //         setters[i] = "set" + attributs[i].getName().substring(0, 1).toUpperCase() + attributs[i].getName().substring(1);
    //     }
    
    //     for (int i = 0; i < attributs.length; i++) {
    //         Method set = objet.getClass().getDeclaredMethod(setters[i], attributs[i].getType());
    
    //         String[] parameter = request.getParameterValues(attributs[i].getName());
    //         if (parameter != null) {
    //             set.invoke(objet, castStringToType(parameter, attributs[i].getType()));
    //         }
    //     }
    // }


    // public static void setObject(HttpServletRequest request, HttpServletResponse response, Object objet) throws Exception {
    //     Field[] attributs = objet.getClass().getDeclaredFields();

    //     for (Field field : attributs) {
    //         if (field.isAnnotationPresent(AnnotationAttribute.class)) {
    //             AnnotationAttribute annotation = field.getAnnotation(AnnotationAttribute.class);
    //             String fieldName = annotation.value();
    //             String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

    //             Method set = objet.getClass().getDeclaredMethod(setterName, field.getType());

    //             String[] parameter = request.getParameterValues(fieldName);
    //             if (parameter != null) {
    //                 set.invoke(objet, castStringToType(parameter, field.getType()));
    //             }
    //         }
    //     }
    // }


    
    // public static <T> T castStringToType(String[] value, Class<T> type) {
    //     if(value==null){
    //         return null;
    //     }
    //     if(!type.isArray()){
    //         if (type == String.class) {
    //             return (T) value[0];
    //         } else if (type == Integer.class || type == int.class) {
    //             return (T) Integer.valueOf(value[0]);
    //         } else if (type == Double.class || type == double.class) {
    //             return (T) Double.valueOf(value[0]);
    //         } else if (type == Float.class || type == float.class) {
    //             return (T) Float.valueOf(value[0]);
    //         } else if (type == Boolean.class || type == boolean.class) {
    //             return (T) Boolean.valueOf(value[0]);
    //         } else if (type == Date.class) {
    //             return (T) Date.valueOf(value[0]);
    //         } else {
    //             throw new IllegalArgumentException("Unsupported type: " + type.getName());
    //         }
    //     }else{
    //         if (type == String[].class) {
    //             return (T) value;
    //         } else if (type == Integer[].class) {
    //             Integer[] tab = new Integer[value.length];
    //             for (int i = 0; i < value.length; i++) {
    //                 tab[i] = Integer.valueOf(value[i]);
    //             }
    //             return (T) tab;
    //         } else if (type == Double[].class) {
    //             Double[] tab = new Double[value.length];
    //             for (int i = 0; i < value.length; i++) {
    //                 tab[i] = Double.valueOf(value[i]);
    //             }
    //             return (T) tab;
    //         } else if (type == Float[].class) {
    //             Float[] tab = new Float[value.length];
    //             for (int i = 0; i < value.length; i++) {
    //                 tab[i] = Float.valueOf(value[i]);
    //             }
    //             return (T) tab;
    //         } else if(type == int[].class){
    //             int[] tab = new int[value.length];
    //             for (int i = 0; i < value.length; i++) {
    //                 tab[i] = Integer.valueOf(value[i]);
    //             }
    //             return (T) tab;
    //         }else if( type == double[].class){
    //             double[] tab = new double[value.length];
    //             for (int i = 0; i < value.length; i++) {
    //                 tab[i] = Double.valueOf(value[i]);
    //             }
    //             return (T) tab;
    //         }else if(type == float[].class){
    //             float[] tab = new float[value.length];
    //             for (int i = 0; i < value.length; i++) {
    //                 tab[i] = Float.valueOf(value[i]);
    //             }
    //             return (T) tab;
    //         }else {
    //             throw new IllegalArgumentException("Unsupported type: " + type.getName());
    //         }
    //     }
    // }

}
