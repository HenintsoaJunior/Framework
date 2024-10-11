package etu2802;

public class VerbAction {
    private String method;
    private String verb;

    public VerbAction() {
    }

    public VerbAction(String method, String verb) {
        this.method = method;
        this.verb = verb;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }
}