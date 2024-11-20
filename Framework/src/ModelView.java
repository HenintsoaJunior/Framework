package etu2802;

import java.util.HashMap;

public class ModelView {
    private String view;
    private HashMap<String,Object> data;

    public ModelView() {
        this.data = new HashMap<String,Object>();
    }
    
    public String getView() {
        return this.view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public HashMap<String, Object> getData() {
        if (this.data == null) {
            this.data = new HashMap<String,Object>();
        }
        return this.data;
    }

    public void setData(HashMap<String, Object> data) {
        if (data == null) {
            this.data = new HashMap<String,Object>();
        } else {
            this.data = data;
        }
    }

    public void addItem(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<String,Object>();
        }
        if (key != null && value != null) {
            this.data.put(key, value);
        }
    }
}