package etu2802;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import etu2802.Annotations.GET;
import etu2802.Annotations.POST;
import etu2802.Annotations.URL;


public class Utils {
    


    public static void checkPackageExists(String packageName, ServletContext context) {
        String packagePath = "/WEB-INF/classes/" + packageName.replace('.', '/');
        Set<String> resourcePaths = context.getResourcePaths(packagePath);
        if (resourcePaths == null || resourcePaths.isEmpty()) {
            throw new IllegalArgumentException("Le package " + packageName + " n'existe pas ou n'est pas configuré dans init-param.");
        }
    }

    public static Method getMethodByUrl(Object objet, String mappingUrlKey, HashMap<String, Mapping> mappingUrls, String httpMethod) throws Exception {
        Method[] allMethods = objet.getClass().getDeclaredMethods();
    
        for (Method method : allMethods) {
            if (method.isAnnotationPresent(URL.class)) {
                URL url = method.getAnnotation(URL.class);
                if (url.lien().equals(mappingUrlKey)) {
                    // Vérifier la présence d'annotations @GET ou @POST
                    boolean hasGET = method.isAnnotationPresent(GET.class);
                    boolean hasPOST = method.isAnnotationPresent(POST.class);
    
                    if (hasGET && hasPOST) {
                        throw new Exception("La méthode " + method.getName() + " ne doit avoir que @GET ou @POST, mais pas les deux.");
                    }
    
                    if (httpMethod.equals("GET")) {
                        if (hasGET || (!hasGET && !hasPOST)) {
                            return method;
                        }
                    } else if (httpMethod.equals("POST")) {
                        if (hasPOST) {
                            return method;
                        }
                    }
    
                    throw new Exception("Méthode HTTP non supportée pour cette URL : " + mappingUrlKey);
                }
            }
        }
    
        throw new Exception("Aucune méthode trouvée pour l'URL donnée et la méthode HTTP");
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
                                if (method.isAnnotationPresent(URL.class)) {
                                    URL url = method.getAnnotation(URL.class);

                                    // Vérification qu'une seule annotation @GET ou @POST est présente
                                    boolean hasGET = method.isAnnotationPresent(GET.class);
                                    boolean hasPOST = method.isAnnotationPresent(POST.class);

                                    if (hasGET && hasPOST) {
                                        throw new IllegalArgumentException("Method " + method.getName() + " cannot have both @GET and @POST annotations.");
                                    }

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
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String httpMethod = request.getMethod();
            Method method = getMethodByUrl(object, mappingUrlKey, mappingUrls, httpMethod);
            Object[] obj = getMethodParams(method, request, object);
            Object returnValue = method.invoke(object, obj);

            if (method.isAnnotationPresent(Annotations.Restapi.class)) {
                if (returnValue instanceof ModelView) {
                    Map<String, Object> jsonResponse = new HashMap<>();
                    for (int i = 0; i < obj.length; i++) {
                        String paramName = method.getParameters()[i].getAnnotation(Annotations.AnnotationParameter.class).value();
                        jsonResponse.put(paramName, obj[i]);
                    }
                    String jsonResponseString = new Gson().toJson(jsonResponse);
                    out.println(jsonResponseString);
                } else {
                    String jsonResponse = new Gson().toJson(returnValue);
                    out.println(jsonResponse);
                }
            } else {
                if (returnValue instanceof ModelView) {
                    ModelView mv = (ModelView) returnValue;
                    request.setAttribute("data", mv.getData());
                    RequestDispatcher dispatcher = request.getRequestDispatcher(mv.getView());
                    dispatcher.forward(request, response);
                } else {
                    out.println("Unsupported type");
                    throw new IllegalArgumentException("Unsupported type");
                }
            }
        } catch (Exception e) {
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            out.close();
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
    
    public static Object[] getMethodParams(Method method, HttpServletRequest request, Object instance) throws IllegalArgumentException {
        Parameter[] parameters = method.getParameters();
        Object[] methodParams = new Object[parameters.length];

        boolean sessionParameterFound = false;

        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();

            if (paramType.equals(MySession.class)) {
                methodParams[i] = new MySession(request.getSession());
                sessionParameterFound = true;
                continue;
            }

            String paramName;
            if (parameters[i].isAnnotationPresent(Annotations.AnnotationParameter.class)) {
                paramName = parameters[i].getAnnotation(Annotations.AnnotationParameter.class).value();
            } else if (parameters[i].isAnnotationPresent(Annotations.AnnotationAttribute.class)) {
                paramName = parameters[i].getAnnotation(Annotations.AnnotationAttribute.class).value();
            } else {
                paramName = parameters[i].getName();
            }

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
                    throw new IllegalArgumentException("ETU002802 Erreur Annotation n'existe pas");
                }
                methodParams[i] = typage(paramValue, paramName, paramType);
            }
        }

        if (!sessionParameterFound) {
            try {
                Field sessionField = findFieldOfType(instance.getClass(), MySession.class);
                if (sessionField != null) {
                    sessionField.setAccessible(true);
                    MySession mySession = (MySession) sessionField.get(instance);
                    if (mySession == null) {
                        mySession = new MySession(request.getSession());
                        sessionField.set(instance, mySession);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Error accessing MySession field in the class", e);
            }
        }

        return methodParams;
    }
    private static Field findFieldOfType(Class<?> clazz, Class<?> fieldType) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(fieldType)) {
                return field;
            }
        }
        return null;
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
        }else if (paramType == Date.class) {
            o = Date.valueOf(paramValue);
        } else {
            o = paramValue;
        }
        return o;
    }
}
