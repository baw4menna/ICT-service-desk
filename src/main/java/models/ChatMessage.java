package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ChatMessage {

    public enum MessageType { USER, AI, SYSTEM }

    private final String id;
    private String content;
    private MessageType type;
    private final LocalDateTime timestamp;
    private final String sender;
    private boolean read;

    public ChatMessage(String content, MessageType type, String sender) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.type = type;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public MessageType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getTimestampFormatted() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    public String getSender() { return sender; }
    public boolean isRead() { return read; }

    public void setContent(String content) { this.content = content; }
    public void setRead(boolean read) { this.read = read; }

    public boolean isUserMessage() { return type == MessageType.USER; }
    public boolean isAIMessage() { return type == MessageType.AI; }
    public boolean isSystemMessage() { return type == MessageType.SYSTEM; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", getTimestampFormatted(), sender, content);
    }
}
