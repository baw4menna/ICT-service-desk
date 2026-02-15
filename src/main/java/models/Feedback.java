package models;

import java.time.LocalDateTime;

public class Feedback {
    private String ticketId;
    private String userName;
    private int rating;          // 1–5
    private String comment;
    private LocalDateTime submittedAt;

    public Feedback(String ticketId, String userName, int rating, String comment, LocalDateTime submittedAt) {
        this.ticketId = ticketId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.submittedAt = submittedAt;
    }

    public String getTicketId() { return ticketId; }
    public String getUserName() { return userName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
}
