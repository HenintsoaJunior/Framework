package etu2802;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

import etu2802.Annotations.Autentificate;
import etu2802.Annotations.GET;
import etu2802.Annotations.POST;
import etu2802.Annotations.PUBLIC;
import etu2802.Annotations.URL;
import etu2802.validation.ValidationManager;
import etu2802.validation.ValidationManager.ValidationResult;


public class Utils {
    


    public static void checkPackageExists(String packageName, ServletContext context) {
        String packagePath = "/WEB-INF/classes/" + packageName.replace('.', '/');
        Set<String> resourcePaths = context.getResourcePaths(packagePath);
        if (resourcePaths == null || resourcePaths.isEmpty()) {
            throw new IllegalArgumentException("Le package " + packageName + " n'existe pas ou n'est pas configuré dans init-param.");
        }
    }

    public static Method getMethodByUrl(Object objet, String mappingUrlKey, HashMap<String, Mapping> mappingUrls, String httpMethod) throws Exception {
    
        Mapping mapping = mappingUrls.get(mappingUrlKey);
        if (mapping == null) {
            throw new Exception("Aucun mapping trouvé pour l'URL donnée : " + mappingUrlKey);
        }
    
    
        Set<VerbAction> verbActions = mapping.getVerbActions();
        if (verbActions == null || verbActions.isEmpty()) {
            throw new Exception("Aucune action trouvée pour l'URL : " + mappingUrlKey);
        }
    
        // Vérification pour éviter plusieurs méthodes pour le même HTTP verb
        validateUniqueHttpVerbs(verbActions);
    
        // Recherche de la méthode correspondant exactement à la méthode HTTP demandée
        Method exactMethod = findMethodByNameAndHttpMethod(objet.getClass(), verbActions, httpMethod);
        if (exactMethod != null) {
            return exactMethod;
        }
    
        // Si aucune méthode exacte n'est trouvée et que c'était une requête POST,
        // recherche d'une méthode GET par défaut
        if (httpMethod.equalsIgnoreCase("POST")) {
            Method defaultGetMethod = findMethodByNameAndHttpMethod(objet.getClass(), verbActions, "GET");
            if (defaultGetMethod != null) {
                return defaultGetMethod;
            }
        }
    
        // Si toujours rien trouvé, chercher une méthode sans annotation spécifique
        for (VerbAction verbAction : verbActions) {
            Method method = findMethodWithoutHttpAnnotation(objet.getClass(), verbAction.getMethod());
            if (method != null) {
                return method;
            }
        }
    
        throw new Exception("Aucune méthode correspondante trouvée pour " + httpMethod + " " + mappingUrlKey);
    }
    
    private static void validateUniqueHttpVerbs(Set<VerbAction> verbActions) throws IllegalArgumentException {
        HashMap<String, String> verbToMethodMap = new HashMap<>();
        for (VerbAction verbAction : verbActions) {
            String httpVerb = verbAction.getVerb().toUpperCase(); // Exemple: "GET" ou "POST"
            String methodName = verbAction.getMethod();
            
            if (verbToMethodMap.containsKey(httpVerb)) {
                String existingMethod = verbToMethodMap.get(httpVerb);
                throw new IllegalArgumentException(
                    "Conflit: Les méthodes '" + existingMethod + "' et '" + methodName + "' utilisent le même VERB HTTP (" + httpVerb + ")."
                );
            }
            
            verbToMethodMap.put(httpVerb, methodName);
        }
    }
        
    private static Method findMethodByNameAndHttpMethod(Class<?> clazz, Set<VerbAction> verbActions, String httpMethod) {
        for (VerbAction verbAction : verbActions) {
            Method method = clazz.getDeclaredMethods().length > 0 ? findMethodWithHttpMethod(clazz, verbAction.getMethod(), httpMethod) : null;
            if (method != null) {
                return method;
            }
        }
        return null;
    }
    
    private static Method findMethodWithHttpMethod(Class<?> clazz, String methodName, String httpMethod) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                if ((method.isAnnotationPresent(Annotations.GET.class) && httpMethod.equalsIgnoreCase("GET")) ||
                    (method.isAnnotationPresent(Annotations.POST.class) && httpMethod.equalsIgnoreCase("POST"))) {
                    return method;
                }
            }
        }
        return null;
    }
    
    private static Method findMethodWithoutHttpAnnotation(Class<?> clazz, String methodName) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && 
                !method.isAnnotationPresent(Annotations.GET.class) && 
                !method.isAnnotationPresent(Annotations.POST.class)) {
                return method;
            }
        }
        return null;
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
                    
                                    Mapping map = mappingUrl.get(url.lien());
                                    if (map == null) {
                                        map = new Mapping(myClass.getSimpleName());
                                        mappingUrl.put(url.lien(), map);
                                    }
                    
                                    if (method.isAnnotationPresent(GET.class)) {
                                        map.addVerbAction(new VerbAction(method.getName(), "GET"));
                                    }
                                    if (method.isAnnotationPresent(POST.class)) {
                                        map.addVerbAction(new VerbAction(method.getName(), "POST"));
                                    }
                                    
                                    // Si aucune annotation HTTP n'est présente, ajouter GET par défaut
                                    if (!method.isAnnotationPresent(GET.class) && !method.isAnnotationPresent(POST.class)) {
                                        map.addVerbAction(new VerbAction(method.getName(), "GET"));
                                    }
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

    public static void dispatchModelView(HttpServletRequest request, HttpServletResponse response, 
                                    Object object, String mappingUrlKey, 
                                    HashMap<String, Mapping> mappingUrls) throws Exception {
        ModelView currentView = null;
        try {
            String httpMethod = request.getMethod();
            Method method = getMethodByUrl(object, mappingUrlKey, mappingUrls, httpMethod);
            
            if (method == null) {
                throw new Exception("Méthode non trouvée pour l'URL : " + mappingUrlKey);
            }

            if (!isMethodAccessible(method, request)) {
                throw new Exception("CETTE PAGE N'EST PAS ACCESSIBLE");
            }

            try {
                Object[] params = getMethodParams(method, request, object);
                Object returnValue = method.invoke(object, params);
                
                if (method.isAnnotationPresent(Annotations.Restapi.class)) {
                    handleRestApiResponse(response, method, params, returnValue);
                } else {
                    if (returnValue instanceof ModelView) {
                        currentView = (ModelView) returnValue;
                        
                        request.setAttribute("data", currentView.getData());
                        for (Map.Entry<String, Object> entry : currentView.getData().entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }
                        RequestDispatcher dispatcher = request.getRequestDispatcher(currentView.getView());
                        dispatcher.forward(request, response);
                    } else {
                        throw new IllegalArgumentException("Type de retour non supporté");
                    }
                }
            } catch (ValidationException ve) {
                handleValidationError(request, response, ve, currentView);
            }
        } catch (Exception e) {
            if (e instanceof ValidationException) {
                handleValidationError(request, response, (ValidationException) e, currentView);
            } else {
                handleGenericError(request, response, e, currentView);
            }
        }
    }
    
    private static boolean isMethodAccessible(Method method, HttpServletRequest request) {
        Class<?> declaringClass = method.getDeclaringClass();
        
        // Check for @PUBLIC annotation first (highest priority)
        if (method.isAnnotationPresent(PUBLIC.class)) {
            return true;
        }
        System.out.println("declaring class "+declaringClass.getName()+" ANNOTATION ENTENTIFICATE  "+declaringClass.isAnnotationPresent(Autentificate.class));
        
        if (declaringClass.isAnnotationPresent(Autentificate.class)) {
            Autentificate classAuth = declaringClass.getAnnotation(Autentificate.class);
            String requiredRole = classAuth.value();
            
            // Authentication check for class-level annotation
            HttpSession httpSession = request.getSession(false);
            
            // If no session exists, authentication fails
            if (httpSession == null) {
                return false;
            }
            
            MySession session = new MySession(httpSession);
            Object userRoles = session.get(etu2802.config.Config.getSESSION());
            
            // If no roles in session, authentication fails
            if (userRoles == null) {
                return false;
            }
            
            // If no specific role required, just having a session is enough
            if (requiredRole.isEmpty()) {
                return true;
            }
            
            // Check if user has the required role
            if (userRoles instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) userRoles;
                
                return roles.stream()
                    .anyMatch(role -> role != null && role.equals(requiredRole));
            }
            
            return false;
        }
        
        // Check method-level authentication
        if (method.isAnnotationPresent(Autentificate.class)) {
            Autentificate methodAuth = method.getAnnotation(Autentificate.class);
            String requiredRole = methodAuth.value();
            
            HttpSession httpSession = request.getSession(false);
            
            // If no session exists, authentication fails
            if (httpSession == null) {
                return false;
            }
            
            MySession session = new MySession(httpSession);
            Object userRoles = session.get(etu2802.config.Config.getSESSION());
            
            // If no roles in session, authentication fails
            if (userRoles == null) {
                return false;
            }
            
            // If no specific role required, just having a session is enough
            if (requiredRole.isEmpty()) {
                return true;
            }
            
            // Check if user has the required role
            if (userRoles instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) userRoles;
                
                return roles.stream()
                    .anyMatch(role -> role != null && role.equals(requiredRole));
            }
            
            return false;
        }
        
        // If no authentication annotations, method is accessible
        return true;
    }

    public static Object[] getMethodParams(Method method, HttpServletRequest request, Object instance) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] methodParams = new Object[parameters.length];
        boolean isMultipart = request.getContentType() != null &&
                              request.getContentType().toLowerCase().startsWith("multipart/form-data");
        ValidationManager validationManager = new ValidationManager();
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
                    Map<String, String> fieldValues = new HashMap<>(); // Pour stocker les valeurs des champs
    
                    for (Field field : fields) {
                        String fieldName = field.getName();
                        field.setAccessible(true);
    
                        if (isMultipart && field.getType() == FileUpload.class) {
                            handleFileUpload(request, paramName, fieldName, field, paramObject);
                        } else {
                            String fieldValue = getFieldValue(request, isMultipart, paramName, fieldName);
                            if (fieldValue != null) {
                                fieldValues.put(fieldName, fieldValue); // Stocker la valeur
                                Object typedValue = typage(fieldValue, paramName + "." + fieldName, field.getType());
                                field.set(paramObject, typedValue);
                            }
                        }
                    }
    
                    ValidationResult validationResult = validationManager.validateWithDetails(paramObject);
                    if (!validationResult.isValid()) {
                        StringBuilder errorDetails = new StringBuilder();
                        errorDetails.append("Validation failed for parameter '").append(paramName).append("':\n");

                        for (Map.Entry<String, List<String>> errorEntry : validationResult.getErrors().entrySet()) {
                            String fieldName = errorEntry.getKey();
                            List<String> errorMessages = errorEntry.getValue();

                            StringBuilder fieldErrors = new StringBuilder();
                            for (String errorMessage : errorMessages) {
                                fieldErrors.append("  Error: ").append(errorMessage).append("\n");
                            }

                            errorDetails.append("- Field '").append(fieldName).append("':\n")
                                        .append(fieldErrors);
                        }

                        System.out.println(errorDetails.toString());

                        throw new ValidationException(errorDetails.toString(), validationResult);
                    }

    
                    methodParams[i] = paramObject;
    
                } catch (ValidationException e) {
                    throw e;
                }
            } else {
                String paramValue = getParameterValue(request, isMultipart, paramName);
                if (paramValue == null) {
                    throw new IllegalArgumentException("ETU002802 Erreur: Le paramètre '" + paramName + 
                                                        "' n'existe pas ou n'a pas d'annotation");
                }
                methodParams[i] = typage(paramValue, paramName, paramType);
            }
        }
    
        handleSessionField(instance, request, sessionParameterFound);
        return methodParams;
    }
    
    private static void handleValidationError(HttpServletRequest request, HttpServletResponse response, 
        ValidationException ve, ModelView currentView) throws ServletException, IOException {

        String originalPage;
        String referer = request.getHeader("Referer");

        if (referer == null || referer.isEmpty()) {
            originalPage = request.getContextPath() + "/error.jsp";
        } else {
            try {
                java.net.URI uri = new java.net.URI(referer);
                originalPage = uri.getPath();

                if (request.getContextPath() != null && !request.getContextPath().isEmpty()) {
                    originalPage = originalPage.substring(request.getContextPath().length());
                }
            } catch (java.net.URISyntaxException e) {
                originalPage = request.getContextPath() + "/error.jsp";
            }
        }

        System.out.println("Errors: ");
        for (Map.Entry<String, List<String>> entry : ve.getValidationResult().getErrors().entrySet()) {
            String fieldName = entry.getKey();
            List<String> fieldErrors = entry.getValue();
            System.out.println("Field: " + fieldName);
            for (String error : fieldErrors) {
                System.out.println("  - " + error);
            }
        }

        request.getSession().setAttribute("errors", ve.getValidationResult().getErrors());
        request.getSession().setAttribute("hasErrors", true);

        Map<String, String> formData = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            if (!ve.getValidationResult().getErrors().containsKey(key) && values.length > 0) {
                formData.put(key, values[0]);
            }
        }
        request.getSession().setAttribute("validFormData", formData);

        response.sendRedirect(request.getContextPath() + originalPage);
    }



    private static void handleRestApiResponse(HttpServletResponse response, Method method, 
        Object[] obj, Object returnValue) throws IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
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
    }

    private static void handleGenericError(HttpServletRequest request, HttpServletResponse response,
        Exception e, ModelView currentView) throws ServletException, IOException {
        
        request.setAttribute("errorMessage", e.getMessage());
        request.setAttribute("hasErrors", true);
        
        String errorPage = (currentView != null) ? currentView.getView() : "/error.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage);
        dispatcher.forward(request, response);
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
    }
    
    
    

    private static void handleFileUpload(HttpServletRequest request, String paramName, 
                                   String fieldName, Field field, Object paramObject) 
                                   throws Exception {
        Part filePart = request.getPart(paramName + "." + fieldName);
        if (filePart != null && filePart.getSize() > 0) {
            FileUpload fileUpload = new FileUpload();
            fileUpload.setName(getFileName(filePart));
            fileUpload.setPath(filePart.getSubmittedFileName());
            
            try (InputStream inputStream = filePart.getInputStream()) {
                byte[] fileBytes = IOUtils.toByteArray(inputStream);
                fileUpload.setBytes(fileBytes);
            }
            
            field.set(paramObject, fileUpload);
        }
    }

    private static String getFieldValue(HttpServletRequest request, boolean isMultipart, 
                                    String paramName, String fieldName) throws Exception {
        if (isMultipart) {
            Part part = request.getPart(paramName + "." + fieldName);
            return part != null ? getValue(part) : null;
        }
        return request.getParameter(paramName + "." + fieldName);
    }

    private static String getParameterValue(HttpServletRequest request, boolean isMultipart, 
                                        String paramName) throws Exception {
        if (isMultipart) {
            Part part = request.getPart(paramName);
            return part != null ? getValue(part) : null;
        }
        return request.getParameter(paramName);
    }

    private static void handleSessionField(Object instance, HttpServletRequest request, 
                                        boolean sessionParameterFound) throws Exception {
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
    }

    public static class ValidationException extends Exception {
        private final ValidationResult validationResult;
        private final String detailedMessage;
    
        public ValidationException(String detailedMessage, ValidationResult validationResult) {
            super(detailedMessage);
            this.detailedMessage = detailedMessage;
            this.validationResult = validationResult;
        }
    
        public ValidationResult getValidationResult() {
            return validationResult;
        }
    
        public String getDetailedMessage() {
            return detailedMessage;
        }
    
        @Override
        public String toString() {
            return detailedMessage;
        }
    }
    
    private static String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private static String getValue(Part part) throws Exception {
        try (InputStream inputStream = part.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Field findFieldOfType(Class<?> clazz, Class<?> fieldType) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(fieldType)) {
                return field;
            }
        }
        return null;
    }
    
    public static Object typage(String paramValue, String paramName, Class<?> paramType) {
        if (paramValue == null || paramValue.isEmpty()) {
            return setPrimitiveDefault(paramType);
        }

        if (paramType.isPrimitive() || paramType == String.class) {
            return castStringToType(paramValue, paramType);
        }

        if (paramType == Date.class || paramType == java.sql.Date.class || paramType == LocalDate.class) {
            try {
                if (paramType == Date.class || paramType == java.sql.Date.class) {
                    return java.sql.Date.valueOf(paramValue);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    return LocalDate.parse(paramValue, formatter);
                }
            } catch (IllegalArgumentException | DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format for parameter: " + paramName);
            }
        }

        if (paramType == byte[].class) {
            return convertToByteArray(paramValue);
        }

        if (paramType == FileUpload.class) {
            FileUpload fichier = new FileUpload();
            fichier.setName(paramValue);
            return fichier;
        }

        throw new IllegalArgumentException("Unsupported type for parameter: " + paramName);
    }

    private static Object castStringToType(String value, Class<?> type) {
        if (type == String.class) return value;
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == long.class || type == Long.class) return Long.parseLong(value);
        if (type == double.class || type == Double.class) return Double.parseDouble(value);
        if (type == float.class || type == Float.class) return Float.parseFloat(value);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);
        if (type == byte.class || type == Byte.class) return Byte.parseByte(value);
        if (type == short.class || type == Short.class) return Short.parseShort(value);
        if (type == char.class || type == Character.class) return value.charAt(0);
        throw new IllegalArgumentException("Unsupported primitive type: " + type.getName());
    }

    private static Object setPrimitiveDefault(Class<?> type) {
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == char.class) return '\u0000';
        return null; // Pour les types non primitifs
    }

    private static byte[] convertToByteArray(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
