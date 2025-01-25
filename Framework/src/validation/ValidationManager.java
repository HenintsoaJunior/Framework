package etu2802.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidationManager {
    
    private Map<Class<?>, Map<String, List<ValidationRule>>> validationRules;
    
    public ValidationManager() {
        this.validationRules = new HashMap<>();
    }
    
    // [Previous annotations remain unchanged...]
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Required {
        String message() default "Le champ est requis";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface MinLength {
        int value();
        String message() default "La longueur minimum n'est pas respectée";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface MaxLength {
        int value();
        String message() default "La longueur maximum est dépassée";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Range {
        double min();
        double max();
        String message() default "La valeur n'est pas dans l'intervalle autorisé";
    }
    
    private interface ValidationRule {
        boolean validate(Object value);
        String getMessage();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Numeric {
        String message() default "Le champ doit être numérique";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Date {
        String format() default "yyyy-MM-dd";
        String message() default "Le champ doit être une date valide au format yyyy-MM-dd";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Email {
        String message() default "Le champ doit contenir un email valide";
    }
    
    public void addRequiredRule(Class<?> clazz, String fieldName) {
        addValidationRule(clazz, fieldName, new ValidationRule() {
            @Override
            public boolean validate(Object value) {
                return value != null && (!(value instanceof String) || !((String) value).isEmpty());
            }
            
            @Override
            public String getMessage() {
                return "Le champ " + fieldName + " est requis";
            }
        });
    }
    
    public void addMaxLengthRule(Class<?> clazz, String fieldName, int maxLength) {
        addValidationRule(clazz, fieldName, new ValidationRule() {
            @Override
            public boolean validate(Object value) {
                if (value == null) return true;
                if (value instanceof String) {
                    return ((String) value).length() <= maxLength;
                }
                return true;
            }
            
            @Override
            public String getMessage() {
                return "Le champ " + fieldName + " ne doit pas dépasser " + maxLength + " caractères";
            }
        });
    }
    
    private void addValidationRule(Class<?> clazz, String fieldName, ValidationRule rule) {
        validationRules.computeIfAbsent(clazz, k -> new HashMap<>())
                      .computeIfAbsent(fieldName, k -> new ArrayList<>())
                      .add(rule);
    }
    
    public boolean validate(Object object) {
        ValidationResult result = validateWithDetails(object);
        return result.isValid();
    }
    
    public ValidationResult validateWithDetails(Object object) {
        ValidationResult result = new ValidationResult();
        
        if (object == null) {
            result.addError("object", "L'objet ne peut pas être null");
            return result;
        }

        Class<?> clazz = object.getClass();
        
        // Validate annotations
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            validateField(field, object, result);
        }
        
        // Validate programmatic rules
        Map<String, List<ValidationRule>> classRules = validationRules.get(clazz);
        if (classRules != null) {
            for (Map.Entry<String, List<ValidationRule>> entry : classRules.entrySet()) {
                try {
                    Field field = clazz.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    Object value = field.get(object);
                    
                    for (ValidationRule rule : entry.getValue()) {
                        if (!rule.validate(value)) {
                            result.addError(entry.getKey(), rule.getMessage());
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    result.addError(entry.getKey(), "Erreur d'accès au champ: " + e.getMessage());
                }
            }
        }
        
        return result;
    }
    
    private void validateField(Field field, Object object, ValidationResult result) {
        try {
            Object value = field.get(object);
            String fieldName = field.getName();
    
            // Required validation
            if (field.isAnnotationPresent(Required.class)) {
                Required required = field.getAnnotation(Required.class);
                if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                    result.addError(fieldName, required.message());
                }
            }
    
            if (value != null) {
                // MinLength validation
                if (field.isAnnotationPresent(MinLength.class)) {
                    MinLength minLength = field.getAnnotation(MinLength.class);
                    if (value instanceof String && ((String) value).length() < minLength.value()) {
                        result.addError(fieldName, minLength.message());
                    } else if (value instanceof Number) {
                        String numberAsString = value.toString();
                        if (numberAsString.length() < minLength.value()) {
                            result.addError(fieldName, minLength.message());
                        }
                    }
                }

                // MaxLength validation
                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength maxLength = field.getAnnotation(MaxLength.class);
                    if (value instanceof String && ((String) value).length() > maxLength.value()) {
                        result.addError(fieldName, maxLength.message());
                    } else if (value instanceof Number) {
                        String numberAsString = value.toString();
                        if (numberAsString.length() > maxLength.value()) {
                            result.addError(fieldName, maxLength.message());
                        }
                    }
                }

    
                // Range validation
                if (field.isAnnotationPresent(Range.class)) {
                    Range range = field.getAnnotation(Range.class);
                    if (value instanceof Number) {
                        double numValue = ((Number) value).doubleValue();
                        if (numValue < range.min() || numValue > range.max()) {
                            result.addError(fieldName, range.message());
                        }
                    }
                }
    
                // Numeric validation
                if (field.isAnnotationPresent(Numeric.class)) {
                    Numeric numeric = field.getAnnotation(Numeric.class);
                    if (!(value instanceof Number)) {
                        result.addError(fieldName, numeric.message());
                    }
                }
    
                // Date validation
                if (field.isAnnotationPresent(Date.class)) {
                    Date date = field.getAnnotation(Date.class);
                    if (value instanceof String) {
                        // Validate date format
                    } else {
                        result.addError(fieldName, date.message());
                    }
                }

                if (field.isAnnotationPresent(Email.class)) {
                    Email email = field.getAnnotation(Email.class);
                    if (value instanceof String && !isValidEmail((String) value)) {
                        result.addError(fieldName, email.message());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            result.addError(field.getName(), "Erreur d'accès au champ: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
  
    }
    
    public static class ValidationResult {
        private boolean valid;
        private Map<String, List<String>> errors;
    
        public ValidationResult() {
            this.valid = true;
            this.errors = new HashMap<>();
        }
    
        // Ajoute une erreur pour un champ spécifique
        public void addError(String fieldName, String error) {
            this.valid = false;
            this.errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(error);
        }
    
        public boolean isValid() {
            return valid;
        }
    
        public Map<String, List<String>> getErrors() {
            return errors;
        }
    
        @Override
        public String toString() {
            if (valid) {
                return "La validation a réussi";
            }
            StringBuilder sb = new StringBuilder("Échec de la validation:\n");
            for (Map.Entry<String, List<String>> entry : errors.entrySet()) {
                sb.append(entry.getKey()).append(": \n");
                for (String error : entry.getValue()) {
                    sb.append(" - ").append(error).append("\n");
                }
            }
            return sb.toString();
        }
    }
    
}