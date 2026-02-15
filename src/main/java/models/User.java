package models;

public class User {

    private String email;
    private String password;
    private String role;      // "ADMIN", "STUDENT", "AGENT"
    private String username;  // full name shown on dashboards

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ---------- getters / setters ----------

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // full name / username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin()   { return "ADMIN".equalsIgnoreCase(role); }
    public boolean isStudent() { return "STUDENT".equalsIgnoreCase(role); }
    public boolean isAgent()   { return "AGENT".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        if (username != null && !username.isEmpty()) {
            return username + " (" + email + ") - " + role;
        }
        return email + " - " + role;
    }
}
