package etu2802;

import javax.servlet.http.HttpSession;

public class MySession {
    private HttpSession session;

    
    public MySession(HttpSession session) {
        this.session = session;
    }

    public Object get(String key) {
        return session.getAttribute(key);
    }

    public void add(String var1, Object var2) {
        this.session.setAttribute(var1, var2);
    }
    

    public void delete(String key) {
        session.removeAttribute(key);
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }
}
