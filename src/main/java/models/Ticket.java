package models;

public class Ticket {
    private String id;
    private String title;
    private String description;
    private String status;        // e.g. NEW, IN_PROGRESS, RESOLVED
    private String studentEmail;

    private String category;      // new
    private String priority;      // new
    private String assignedTo;    // agent name/email
    private String createdAt;     // simple string timestamp

    public Ticket(String title, String description, String status, String studentEmail) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.title = title;
        this.description = description;
        this.status = status;
        this.studentEmail = studentEmail;
        this.createdAt = java.time.LocalDateTime.now().toString();
    }

    // ----- getters -----
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getStudentEmail() { return studentEmail; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public String getAssignedTo() { return assignedTo; }
    public String getCreatedAt() { return createdAt; }

    // ----- setters -----
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public void setCategory(String category) { this.category = category; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    private String createdDate;
    private String updatedDate;
    private String resolutionNotes;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

}

