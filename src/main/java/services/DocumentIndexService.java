package services;

import models.KnowledgeDocument;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentIndexService {

    private final List<KnowledgeDocument> documents = new ArrayList<>();

    public DocumentIndexService() {
        initializeDefaultDocuments();
    }

    private void initializeDefaultDocuments() {
        addDocument("WiFi Connection Problems",
                "If WiFi is not working:\n1. Forget the eduroam network\n2. Reconnect using your student email and password\n3. Move closer to an access point\n4. Restart your device\n5. Contact IT if problem persists",
                "WiFi", "Troubleshooting");

        addDocument("How to Reset Password",
                "To reset your password:\n1. Go to the login page\n2. Click 'Forgot Password'\n3. Enter your student email\n4. Check email for reset link\n5. Follow the link to set new password",
                "Password", "FAQ");

        addDocument("LMS Login Error",
                "If you cannot access LMS:\n1. Clear your browser cache\n2. Try an incognito window\n3. Verify your credentials\n4. Ensure you are enrolled in the course\n5. Contact IT support if still having issues",
                "LMS", "Troubleshooting");

        addDocument("Printing Issues",
                "For printing problems:\n1. Check your print balance\n2. Select the correct printer\n3. Confirm document is PDF\n4. Try from a different device\n5. Visit the library for assistance",
                "Printing", "Troubleshooting");

        System.out.println("✓ Knowledge base loaded: " + documents.size() + " documents");
    }

    public void addDocument(String title, String content, String category, String source) {
        documents.add(new KnowledgeDocument(title, content, category, source));
    }

    public List<KnowledgeDocument> searchDocuments(String query) {
        if (query == null || query.trim().isEmpty()) return List.of();

        String[] terms = query.toLowerCase().split("\\s+");
        for (KnowledgeDocument doc : documents) {
            double score = calculateRelevance(doc, terms);
            doc.setRelevanceScore(score);
        }

        return documents.stream()
                .filter(d -> d.getRelevanceScore() > 0)
                .sorted(Comparator.comparingDouble(KnowledgeDocument::getRelevanceScore).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private double calculateRelevance(KnowledgeDocument doc, String[] queryTerms) {
        String text = (doc.getTitle() + " " + doc.getContent()).toLowerCase();
        double score = 0;
        for (String t : queryTerms) {
            if (t.length() < 2) continue;
            if (doc.getTitle().toLowerCase().contains(t)) score += 3;

            int idx = 0, count = 0;
            while ((idx = text.indexOf(t, idx)) != -1) {
                count++;
                idx += t.length();
            }
            score += count;
        }
        return score;
    }

    public List<String> getCategories() {
        return documents.stream()
                .map(KnowledgeDocument::getCategory)
                .distinct()
                .sorted()
                .toList();
    }

    public int getDocumentCount() {
        return documents.size();
    }
}
