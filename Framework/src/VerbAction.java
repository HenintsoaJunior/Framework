package etu2802;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerbAction that = (VerbAction) o;
        return Objects.equals(method, that.method) && Objects.equals(verb, that.verb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, verb);
    }

    @Override
    public String toString() {
        return "VerbAction{" +
                "method='" + method + '\'' +
                ", verb='" + verb + '\'' +
                '}';
    }
}
