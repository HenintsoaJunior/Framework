package etu2802.validation;

import java.util.List;
import java.util.Map;

public class FormErrorHandler {

    public static String getErrorClass(Map<String, List<String>> errors, String fieldName) {
        return errors.containsKey(fieldName) ? "has-error" : "";
    }

    public static String renderErrors(Map<String, List<String>> errors, String fieldName) {
        StringBuilder html = new StringBuilder();
        if (errors.containsKey(fieldName)) {
            List<String> fieldErrors = errors.get(fieldName);
            for (String error : fieldErrors) {
                html.append("<span class=\"error-message\">").append(error).append("</span>");
            }
        }
        return html.toString();
    }

    public static String getValueOrDefault(Map<String, String> validFormData, String fieldName, String defaultValue) {
        return validFormData.getOrDefault(fieldName, defaultValue);
    }
}
