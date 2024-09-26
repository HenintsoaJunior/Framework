package etu2802;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotations {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationController {
        String value();
    }
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationAttribute {
        String value();
    }
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationMethod {
        String value(); 
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface AnnotationParameter {
        String value(); 
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Restapi {
        String value() default "";
    }
}