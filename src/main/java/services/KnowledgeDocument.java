package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class KnowledgeDocument {

    private final String id;
    private final String title;
    private final String content;
    private final String category;
    private final String source;
    private final LocalDateTime createdAt;
    private double relevanceScore;

    public KnowledgeDocument(String title, String content, String category, String source) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.category = category;
        this.source = source;
        this.createdAt = LocalDateTime.now();
        this.relevanceScore = 0.0;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public String getSource() { return source; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double score) { this.relevanceScore = score; }
    public String getPreview() {
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
}
