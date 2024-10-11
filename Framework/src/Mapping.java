package etu2802;

import java.util.HashSet;
import java.util.Set;

public class Mapping {
    private String className;
    private Set<VerbAction> verbActions;

    public Mapping() {
        this.verbActions = new HashSet<>();
    }

    public Mapping(String cl) {
        this.setClassName(cl);
        this.verbActions = new HashSet<>();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Set<VerbAction> getVerbActions() {
        return verbActions;
    }

    public void setVerbActions(Set<VerbAction> verbActions) {
        this.verbActions = verbActions;
    }

    public void addVerbAction(VerbAction verbAction) {
        this.verbActions.add(verbAction);
    }
}