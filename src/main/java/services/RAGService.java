package services;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import models.KnowledgeDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RAGService {

    private static ChatLanguageModel model;
    private static DocumentIndexService documentIndex;
    private static List<String> conversationHistory;
    private static boolean initialized = false;
    private static boolean useAI = true;

    private static class FaqItem {
        String question;
        String answer;
        FaqItem(String q, String a) { question = q.toLowerCase(); answer = a; }
    }

    private static final List<FaqItem> FALLBACK_FAQS = new ArrayList<>();

    static {
        FALLBACK_FAQS.add(new FaqItem(
                "wifi not working eduroam campus network",
                "If WiFi is not working, forget the eduroam network, reconnect using your student email and password, and move closer to an access point."
        ));
        FALLBACK_FAQS.add(new FaqItem(
                "forgot password reset student account",
                "Use the 'Forgot Password' link on the portal, check your student email for the reset link, and follow the instructions to set a new password."
        ));
        FALLBACK_FAQS.add(new FaqItem(
                "lms moodle blackboard canvas login error",
                "If you cannot access the LMS, clear your browser cache, try an incognito window, and confirm you are using your correct student credentials."
        ));
        FALLBACK_FAQS.add(new FaqItem(
                "printing error library lab",
                "For printing issues, check your print balance, select the correct printer, and ensure your document is in PDF format before printing."
        ));
    }

    public static void initialize(String apiKey, String modelName, boolean enableAI) {
        if (initialized) return;

        // allow env var OR JVM property (from IDE VM options)
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("OPENAI_API_KEY");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getProperty("OPENAI_API_KEY");
        }

        useAI = enableAI;
        try {
            if (enableAI && apiKey != null && !apiKey.isEmpty()) {
                model = OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName(modelName)
                        .temperature(0.7)
                        .topP(1.0)
                        .maxTokens(1024)
                        .build();
            }
        } catch (Exception e) {
            model = null;
            useAI = false;
        }

        documentIndex = new DocumentIndexService();
        conversationHistory = new ArrayList<>();
        initialized = true;

    }

    public static String getResponse(String userQuery) {
        if (!initialized) {
            return "RAG Service not initialized. Please set your API key.";
        }
        if (userQuery == null || userQuery.isBlank()) {
            return "Please type your question so the ICT service desk can help you.";
        }

        try {
            if (useAI && model != null) {
                return getAIResponse(userQuery);
            }
            return getKeywordResponse(userQuery);
        } catch (Exception e) {
            return getFallbackResponse(userQuery);
        }
    }

    private static String getAIResponse(String userQuery) {
        List<KnowledgeDocument> docs = documentIndex.searchDocuments(userQuery);
        String context = buildContext(docs);
        String prompt = buildPrompt(userQuery, context);
        String response = model.generate(prompt);
        conversationHistory.add("User: " + userQuery);
        conversationHistory.add("Assistant: " + response);
        return response;
    }

    private static String getKeywordResponse(String userQuery) {
        String q = userQuery.toLowerCase();
        FaqItem best = null;
        int bestScore = 0;

        for (FaqItem item : FALLBACK_FAQS) {
            int score = calculateSimilarity(q, item.question);
            if (score > bestScore) {
                bestScore = score;
                best = item;
            }
        }

        if (best != null && bestScore >= 2) {
            conversationHistory.add("User: " + userQuery);
            conversationHistory.add("Assistant: " + best.answer);
            return best.answer;
        }

        return "Sorry, no exact FAQ was found. Please submit an ICT ticket so a human agent can help you.";
    }

    private static String getFallbackResponse(String userQuery) {
        String q = userQuery.toLowerCase();

        if (q.contains("wifi") || q.contains("eduroam")) {
            return "For WiFi issues: Forget the network and reconnect using your student email. Move closer to an access point.";
        }
        if (q.contains("password")) {
            return "To reset your password: Use the 'Forgot Password' link on the portal and check your email for the reset link.";
        }
        if (q.contains("lms") || q.contains("moodle") || q.contains("canvas")) {
            return "For LMS issues: Clear your browser cache, try an incognito window, and verify your credentials.";
        }
        if (q.contains("print")) {
            return "For printing issues: Check your balance, choose the correct printer, and ensure the document is PDF.";
        }

        return "Sorry, I couldn't find a solution. Please submit a ticket to the ICT helpdesk.";
    }

    private static String buildContext(List<KnowledgeDocument> documents) {
        if (documents.isEmpty()) return "No specific documentation available.";
        StringBuilder sb = new StringBuilder("RELEVANT INFORMATION:\n");
        sb.append("=".repeat(60)).append("\n");
        int idx = 1;
        for (KnowledgeDocument d : documents) {
            sb.append(String.format("[%d] %s\nCategory: %s\n%s\n",
                    idx++, d.getTitle(), d.getCategory(), d.getContent()));
            sb.append("-".repeat(60)).append("\n");
        }
        return sb.toString();
    }

    private static String buildPrompt(String userQuery, String context) {
        return String.format("""
            You are an ICT Service Desk Assistant for a university.

            KNOWLEDGE BASE:
            %s

            STUDENT QUESTION: %s

            INSTRUCTIONS:
            1. Use the knowledge base information.
            2. Be professional and concise.
            3. Provide steps when needed.
            4. Suggest submitting a ticket if complex.

            RESPONSE:
            """, context, userQuery);
    }

    private static int calculateSimilarity(String user, String stored) {
        int score = 0;
        for (String t : user.split("\\s+")) {
            if (t.length() < 3) continue;
            if (stored.contains(t)) score++;
        }
        return score;
    }

    public static String getRetrievalDetails(String query) {
        if (!initialized || documentIndex == null) return "RAG not initialized";
        List<KnowledgeDocument> docs = documentIndex.searchDocuments(query);
        if (docs.isEmpty()) return "No documents matched the query.";
        return docs.stream()
                .map(d -> "✓ " + d.getTitle() + " (Category: " + d.getCategory() + ")")
                .collect(Collectors.joining("\n"));
    }

    public static String getKnowledgeBaseStats() {
        if (!initialized || documentIndex == null) return "RAG not initialized";
        return String.format("Knowledge Base: %d documents | Categories: %s",
                documentIndex.getDocumentCount(),
                String.join(", ", documentIndex.getCategories()));
    }

    public static String getStatus() {
        return String.format("RAG Status:\n- Initialized: %s\n- AI Mode: %s\n- Model Ready: %s",
                initialized, useAI, model != null);
    }
}
