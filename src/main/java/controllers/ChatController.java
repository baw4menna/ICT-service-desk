package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.ChatMessage;
import models.Conversation;
import services.RAGService;

public class ChatController {

    @FXML private VBox messagesBox;
    @FXML private TextField inputField;
    @FXML private ScrollPane scrollPane;

    private Conversation currentConversation;
    private final String currentUserId = "student_001";

    @FXML
    private void initialize() {
        // 1) Read key from env OR JVM property
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getProperty("OPENAI_API_KEY");
        }

        boolean enableAI = apiKey != null && !apiKey.isEmpty();

        // 2) Initialize RAG
        RAGService.initialize(apiKey, "gpt-3.5-turbo", enableAI);

        // 3) Debug lines – keep for now
        System.out.println("API KEY (len): " + (apiKey == null ? 0 : apiKey.length()));
        System.out.println(RAGService.getStatus());
        System.out.println(RAGService.getKnowledgeBaseStats());

        // 4) Rest of your existing initialize code (conversation, welcome message, etc.)
        currentConversation = new Conversation("Support Chat", currentUserId);

        if (messagesBox != null) {
            messagesBox.setSpacing(10);
            messagesBox.setFillWidth(true);
        }

        String welcome = "Hi, I am the ICT service desk assistant. Ask me about WiFi, passwords, LMS, or printing.";
        addBotMessage(welcome);
        currentConversation.addMessage(
                new ChatMessage(welcome, ChatMessage.MessageType.AI, "ICT Service Desk Assistant")
        );

        inputField.setOnAction(e -> handleSend());
    }

    @FXML
    private void handleSend() {
        String text = inputField.getText();
        if (text == null || text.isBlank()) return;

        addUserMessage(text);
        currentConversation.addMessage(
                new ChatMessage(text, ChatMessage.MessageType.USER, "You")
        );

        Thread thread = new Thread(() -> {
            try {
                String answer = RAGService.getResponse(text);

                Platform.runLater(() -> {
                    addBotMessage(answer);
                    currentConversation.addMessage(
                            new ChatMessage(answer, ChatMessage.MessageType.AI, "ICT Service Desk Assistant")
                    );
                    System.out.println("📚 Retrieved: " + RAGService.getRetrievalDetails(text));
                });
            } catch (Exception e) {
                Platform.runLater(() -> addBotMessage("❌ Error: " + e.getMessage()));
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();

        inputField.clear();
        scrollToBottom();
    }

    @FXML
    public void clearChat() {
        messagesBox.getChildren().clear();
        currentConversation = new Conversation("Support Chat", currentUserId);
        addBotMessage("Hi, I am the ICT service desk assistant. Ask me about WiFi, passwords, LMS, or printing.");
    }

    private void addUserMessage(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle(
                "-fx-background-color: #007bff; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 14; " +
                        "-fx-background-radius: 16; " +
                        "-fx-font-size: 12;"
        );
        HBox container = new HBox(label);
        container.setAlignment(Pos.CENTER_RIGHT);
        container.setPadding(new Insets(5, 10, 5, 40));
        messagesBox.getChildren().add(container);
    }

    private void addBotMessage(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle(
                "-fx-background-color: #e9ecef; " +
                        "-fx-text-fill: #212529; " +
                        "-fx-padding: 10 14; " +
                        "-fx-background-radius: 16; " +
                        "-fx-font-size: 12;"
        );
        HBox container = new HBox(label);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 40, 5, 10));
        messagesBox.getChildren().add(container);
    }

    private void scrollToBottom() {
        if (scrollPane != null) {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        }
    }

    public Conversation getCurrentConversation() {
        return currentConversation;
    }
}
