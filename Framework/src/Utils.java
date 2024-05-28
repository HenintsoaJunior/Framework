package etu2802;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

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

    public static HashMap<String, Mapping> allMappingUrls(ServletContext context,String pckg) {
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
            System.out.println("Class Name null");
        }
        return mappingUrl;
    }
}
