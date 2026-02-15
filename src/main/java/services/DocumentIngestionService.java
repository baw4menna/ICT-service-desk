package services;

import models.KnowledgeBase;
import java.io.File;

public class DocumentIngestionService {

    private KnowledgeBase knowledgeBase;

    public DocumentIngestionService(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    //simple placeholder: just prints file (no real PDF parsing yet)
    public void ingestPDF(String filePath, String title, String category, String uploadedBy) {
        System.out.println("Ingest PDF called");
        System.out.println("Path: " + filePath);
        System.out.println("Title: " + title);
        System.out.println("Category: " + category);
        System.out.println("Uploaded by: " + uploadedBy);
        // later your teammate will add real logic here
    }
}
