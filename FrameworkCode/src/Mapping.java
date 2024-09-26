package etu2802;

public class Mapping {
    String className;
    String method;
    private Class[] parameterTypes;

    public Mapping(){
        
    }

    public Mapping(String cl , String met){
        this.setClassName(cl);
        this.setMethod(met);
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
    
    
}
