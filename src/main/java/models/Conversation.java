package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {

    private final String id;
    private String title;
    private final String userId;
    private final List<ChatMessage> messages;
    private final LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private boolean active;

    public Conversation(String title, String userId) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.userId = userId;
        this.messages = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.active = true;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        lastUpdated = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getUserId() { return userId; }
    public List<ChatMessage> getMessages() { return new ArrayList<>(messages); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public boolean isActive() { return active; }

    public void setTitle(String title) { this.title = title; }
    public void setActive(boolean active) { this.active = active; }

    public int getMessageCount() { return messages.size(); }
    public ChatMessage getLastMessage() {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }
}
