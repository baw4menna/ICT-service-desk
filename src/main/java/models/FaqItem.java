package models;

public class FaqItem {
    private String title;
    private String body;
    private String category;   // e.g., "Access", "Network", "Email"
    private int views;
    private String[] tags;

    public FaqItem(String title, String body, String category, int views, String[] tags) {
        this.title = title;
        this.body = body;
        this.category = category;
        this.views = views;
        this.tags = tags;
    }

    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getCategory() { return category; }
    public int getViews() { return views; }
    public String[] getTags() { return tags; }
}
