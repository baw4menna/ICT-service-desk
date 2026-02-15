package models;

import java.time.LocalDateTime;

public class ActivityLog {
    private String type;        // "User Logout", "Ticket Updated", etc.
    private String description; // the small text under title
    private String user;        // "Sarah Johnson"
    private LocalDateTime time; // timestamp

    public ActivityLog(String type, String description, String user, LocalDateTime time) {
        this.type = type;
        this.description = description;
        this.user = user;
        this.time = time;
    }

    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getUser() { return user; }
    public LocalDateTime getTime() { return time; }
}
