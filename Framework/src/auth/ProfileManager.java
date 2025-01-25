package etu2802.auth;

public class ProfileManager {
    private String role;
    private boolean connected;

    public ProfileManager(String role) {
        this.role = role;
        this.connected = true;
    }

    public String getRole() { 
        return role; 
    }

    public boolean isConnected() { 
        return connected; 
    }

    public void disconnect() { 
        this.connected = false; 
    }
}
