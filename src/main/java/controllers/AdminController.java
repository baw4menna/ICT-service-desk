package controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.ActivityLog;
import models.FaqItem;
import models.KnowledgeBase;
import services.DocumentIngestionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.TicketService;
import models.Ticket;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class AdminController {

    // center pages
    @FXML
    private StackPane contentArea;
    @FXML
    private ScrollPane analyticsPage;
    @FXML
    private ScrollPane feedbackPage;
    @FXML
    private VBox logsPage;
    @FXML
    private VBox faqPage;        // the whole FAQ page (StackPane child)

    // containers inside pages
    @FXML
    private VBox logsContainer;  // inside logsPage ScrollPane
    @FXML
    private VBox faqContainer;   // inside faqPage ScrollPane

    // tabs
    @FXML
    private Button analyticsTab;
    @FXML
    private Button feedbackTab;
    @FXML
    private Button logsTab;
    @FXML
    private Button faqTab;

    // feedback card labels
    @FXML
    private Label fbTitleLabel;
    @FXML
    private Label fbTicketIdLabel;
    @FXML
    private Label fbCommentLabel;
    @FXML
    private Label fbRatingLabel;

    // keep your services for later
    private KnowledgeBase kb = new KnowledgeBase();
    private DocumentIngestionService ingestion = new DocumentIngestionService(kb);

    @FXML
    private void initialize() {
        // Feedback card – placeholder data for now
        fbTitleLabel.setText("Cannot access email account");
        fbTicketIdLabel.setText("Ticket #1");
        fbCommentLabel.setText("nice");
        fbRatingLabel.setText("4 / 5 ★");

        // Build demo activity logs and FAQ lists (placeholders; replace with real data later)
        loadActivityLogs();
        loadFaqItems();

        // Default tab
        showAnalytics();
        // Load real data from file instead of hardcoded
        loadActivityLogs();
        loadFaqItems();
        showAnalytics();
    }

    // Build the activity log rows
    private void loadActivityLogs() {
        if (logsContainer == null) return;
        logsContainer.getChildren().clear();

        List<Ticket> tickets = TicketService.getInstance().getAllTickets();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss a");

        for (Ticket ticket : tickets) {
            BorderPane row = new BorderPane();
            row.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-padding: 10;");

            HBox left = new HBox(8);
            left.setStyle("-fx-alignment: TOP_LEFT;");

            Label icon = new Label("T");
            icon.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 6; -fx-padding: 6 10 6 10;");

            VBox textBox = new VBox(2);
            Label title = new Label("Ticket Created: " + ticket.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            Label desc = new Label("Status: " + ticket.getStatus() + " | " + ticket.getCategory());
            desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #757575;");
            Label time = new Label(ticket.getCreatedAt() != null ? ticket.getCreatedAt() : "-");
            time.setStyle("-fx-font-size: 10px; -fx-text-fill: #b0b0b0;");
            textBox.getChildren().addAll(title, desc, time);

            left.getChildren().addAll(icon, textBox);

            Label user = new Label(ticket.getStudentEmail());
            user.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 14; "
                    + "-fx-padding: 4 10 4 10; -fx-font-size: 11px;");

            row.setLeft(left);
            row.setRight(user);

            logsContainer.getChildren().add(row);
        }
}

    // Build the FAQ cards
    private void loadFaqItems() {
        if (faqContainer == null) return;

        faqContainer.getChildren().clear();

        FaqItem[] items = new FaqItem[] {
                new FaqItem(
                        "How to Reset Your Password",
                        "To reset your password: 1. Go to login page 2. Click \"Forgot Password\" 3. Enter your email 4. Check your email for reset link",
                        "Access",
                        245,
                        new String[] {"password", "reset", "login"}
                ),
                new FaqItem(
                        "VPN Connection Guide",
                        "To connect to VPN: 1. Download VPN client 2. Install and open 3. Enter credentials 4. Select server location 5. Click Connect",
                        "Network",
                        189,
                        new String[] {"vpn", "connection", "network"}
                ),
                new FaqItem(
                        "Email Setup on Mobile Device",
                        "To setup email on mobile: 1. Open email app 2. Add account 3. Select Exchange 4. Enter company email and password 5. Complete setup",
                        "Email",
                        156,
                        new String[] {"email", "mobile", "setup"}
                )
        };

        for (FaqItem item : items) {
            BorderPane card = new BorderPane();
            card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-padding: 12;");

            // Left: title + body + views + tags
            VBox left = new VBox(4);

            Label title = new Label(item.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

            Label body = new Label(item.getBody());
            body.setWrapText(true);
            body.setStyle("-fx-font-size: 11px; -fx-text-fill: #616161;");

            HBox metaRow = new HBox(10);
            Label views = new Label(item.getViews() + " views");
            views.setStyle("-fx-font-size: 10px; -fx-text-fill: #9e9e9e;");

            HBox tagsBox = new HBox(5);
            for (String tagText : item.getTags()) {
                Label tag = new Label(tagText);
                tag.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; "
                        + "-fx-padding: 2 8 2 8; -fx-font-size: 10px;");
                tagsBox.getChildren().add(tag);
            }
            metaRow.getChildren().addAll(views, tagsBox);

            left.getChildren().addAll(title, body, metaRow);

            // Right: category pill
            Label category = new Label(item.getCategory());
            category.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 14; "
                    + "-fx-padding: 4 12 4 12; -fx-font-size: 11px;");

            card.setLeft(left);
            card.setRight(category);

            faqContainer.getChildren().add(card);
        }
    }

    @FXML
    private void showAnalytics() {
        setActivePage(analyticsPage);
        setActiveTab(analyticsTab);
    }

    @FXML
    private void showFeedback() {
        setActivePage(feedbackPage);
        setActiveTab(feedbackTab);
    }

    @FXML
    private void showLogs() {
        setActivePage(logsPage);
        setActiveTab(logsTab);
    }

    @FXML
    private void showFaq() {
        setActivePage(faqPage);
        setActiveTab(faqTab);
    }

    private void setActivePage(Node page) {
        analyticsPage.setVisible(false);
        analyticsPage.setManaged(false);
        feedbackPage.setVisible(false);
        feedbackPage.setManaged(false);
        logsPage.setVisible(false);
        logsPage.setManaged(false);
        faqPage.setVisible(false);
        faqPage.setManaged(false);

        page.setVisible(true);
        page.setManaged(true);
    }

    private void setActiveTab(Button active) {
        resetTab(analyticsTab);
        resetTab(feedbackTab);
        resetTab(logsTab);
        resetTab(faqTab);

        active.setStyle("-fx-background-radius: 20; -fx-background-color: #1976d2; -fx-text-fill: white;");
    }

    private void resetTab(Button b) {
        b.setStyle("-fx-background-radius: 20; -fx-background-color: #e0e0e0;");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fxml/login.fxml")  // or /fxml/loginregister.fxml if that's your file
            );
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
