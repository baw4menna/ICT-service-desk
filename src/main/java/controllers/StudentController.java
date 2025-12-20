package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Ticket;
import models.User;
import services.TicketService;
import services.TicketStore;
import services.UserService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    // HEADER
    @FXML private Label welcomeLabel;

    // NAV CONTENT ROOTS
    @FXML private VBox myTicketsContent;
    @FXML private VBox submitTicketContent;
    @FXML private VBox faqContent;

    // MY TICKETS
    @FXML private VBox ticketsListContainer;
    @FXML private Label ticketCountLabel;

    // SUBMIT TICKET (matches your FXML)
    @FXML private TextField ticketTitleField;     // fx:id="ticketTitleField"
    @FXML private TextArea ticketDescField;      // fx:id="ticketDescField"
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private Button uploadFilesButton;
    @FXML private HBox attachmentsContainer;
    @FXML private Button submitTicketButton;
    @FXML private ScrollPane submitTicketScroll;

    // FAQ
    @FXML private TextField faqSearchField;
    @FXML private VBox faqListContainer;
    // OVERLAY DETAILS
    @FXML private BorderPane ticketDetailsOverlay;
    @FXML private VBox ticketDetailsCard;
    @FXML private Label detailsTitleLabel;
    @FXML private Label detailsTicketIdLabel;
    @FXML private Label detailsStatusBadge;
    @FXML private Label detailsPriorityBadge;
    @FXML private Label detailsDescriptionLabel;
    @FXML private Label detailsCategoryLabel;
    @FXML private Label detailsCreatedLabel;
    @FXML private Label detailsChannelLabel;
    @FXML private Label detailsUpdatedLabel;
    @FXML private Label detailsAssignedToLabel;
    @FXML private VBox chatbotContainer;
    @FXML private ScrollPane faqContentScroll;


    private final UserService userService = UserService.getInstance();
    private final List<File> attachments = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Dynamic welcome text
        User current = userService.getCurrentUser();
        if (current != null && current.getUsername() != null && !current.getUsername().isEmpty()) {
            welcomeLabel.setText("Welcome, " + current.getUsername() + "!");
        } else if (current != null) {
            welcomeLabel.setText("Welcome, " + current.getEmail() + "!");
        } else {
            welcomeLabel.setText("Welcome, Student!");
        }

        // Init category / priority options (example values)
        if (categoryCombo != null) {
            categoryCombo.getItems().setAll("Hardware", "Software", "Network", "Account", "Other");
        }
        if (priorityCombo != null) {
            priorityCombo.getItems().setAll("Low - Minor issue", "Medium - Normal issue", "High - Urgent");
        }

        // Add listener to FAQ search
        if (faqSearchField != null) {
            faqSearchField.textProperty().addListener((obs, oldVal, newVal) -> filterFaq());
        }

        myTicketsContent.setVisible(true);
        myTicketsContent.setManaged(true);

        submitTicketScroll.setVisible(false);
        submitTicketScroll.setManaged(false);

        faqContentScroll.setVisible(false);
        faqContentScroll.setManaged(false);
        showMyTickets(null);
    }

    @FXML
    private void closeTicketDetails(ActionEvent event) {
        ticketDetailsOverlay.setVisible(false);
        ticketDetailsOverlay.setManaged(false);
    }

    @FXML
    private void showMyTickets(ActionEvent event) {
        myTicketsContent.setVisible(true);
        myTicketsContent.setManaged(true);

        submitTicketScroll.setVisible(false);
        submitTicketScroll.setManaged(false);

        faqContentScroll.setVisible(false);
        faqContentScroll.setManaged(false);

        refreshTicketsList();
    }

    @FXML
    private void showSubmitTicket(ActionEvent event) {
        myTicketsContent.setVisible(false);
        myTicketsContent.setManaged(false);

        submitTicketScroll.setVisible(true);
        submitTicketScroll.setManaged(true);

        faqContentScroll.setVisible(false);
        faqContentScroll.setManaged(false);
    }

    @FXML
    private void showFaq(ActionEvent event) {
        myTicketsContent.setVisible(false);
        myTicketsContent.setManaged(false);

        submitTicketScroll.setVisible(false);
        submitTicketScroll.setManaged(false);

        faqContentScroll.setVisible(true);
        faqContentScroll.setManaged(true);
    }

    // ---------- SUBMIT TICKET ----------

    @FXML
    private void handleUploadFiles(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select attachment");
        File file = chooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (file == null) return;

        attachments.add(file);

        Label fileTag = new Label(file.getName());
        fileTag.setStyle("-fx-background-color: #e5e7eb; -fx-padding: 4 8; -fx-background-radius: 6;"
                + "-fx-font-size: 11px; -fx-text-fill: #374151;");
        attachmentsContainer.getChildren().add(fileTag);
    }

    @FXML
    private void submitTicket(ActionEvent event) {
        String title = ticketTitleField.getText() != null ? ticketTitleField.getText().trim() : "";
        String desc  = ticketDescField.getText() != null ? ticketDescField.getText().trim() : "";
        String category = categoryCombo.getValue();
        String priority = priorityCombo.getValue();

        if (title.isEmpty() || desc.isEmpty() || category == null || priority == null) {
            showError("Please fill in all required fields.");
            return;
        }

        User current = userService.getCurrentUser();
        if (current == null) {
            showError("No logged-in user. Please log in again.");
            return;
        }

        Ticket t = new Ticket(
                title,
                desc,
                "OPEN",
                current.getEmail()
        );
        t.setCategory(category);
        t.setPriority(priority);
        t.setCreatedDate(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")));
        TicketService.getInstance().saveTicket(t);
        TicketStore.addTicket(t);                   // KEEP IN-MEMORY FOR THIS SESSION

        ticketTitleField.clear();
        ticketDescField.clear();
        categoryCombo.getSelectionModel().clearSelection();
        priorityCombo.getSelectionModel().clearSelection();
        attachments.clear();
        attachmentsContainer.getChildren().clear();

        showMyTickets(null);
        showInfo("Ticket submitted successfully.");
    }
    // ---------- MY TICKETS RENDERING ----------

    private void refreshTicketsList() {
        ticketsListContainer.getChildren().clear();

        User current = userService.getCurrentUser();
        if (current == null) {
            ticketCountLabel.setText("0 total tickets");
            Label none = new Label("No user logged in.");
            none.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
            ticketsListContainer.getChildren().add(none);
            return;
        }

        String email = current.getEmail();
        int count = 0;

        for (Ticket ticket : TicketService.getInstance().getAllTickets()) {
            if (email.equalsIgnoreCase(ticket.getStudentEmail())) {
                VBox card = createTicketCard(ticket);
                ticketsListContainer.getChildren().add(card);
                count++;
            }
        }

        ticketCountLabel.setText(count + " total tickets");

        if (count == 0) {
            Label none = new Label("No tickets yet. Submit your first ticket.");
            none.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
            ticketsListContainer.getChildren().add(none);
        }
    }

    private VBox createTicketCard(Ticket ticket) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");

        Label title = new Label(ticket.getTitle());
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label status = new Label("Status: " + ticket.getStatus());
        status.setStyle("-fx-text-fill: #5a6c7d; -fx-font-size: 11px;");

        Label meta = new Label();
        String assigned = ticket.getAssignedTo() != null ? ticket.getAssignedTo() : "Unassigned";
        String cat = ticket.getCategory() != null ? ticket.getCategory() : "-";
        String pri = ticket.getPriority() != null ? ticket.getPriority() : "-";
        meta.setText("Category: " + cat + "   Priority: " + pri + "   Assigned to: " + assigned);
        meta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

        Label desc = new Label(ticket.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");

        HBox header = new HBox(5, title);
        header.setSpacing(10);

        card.getChildren().addAll(header, status, meta, desc);

        // click to view full details dialog
        card.setOnMouseClicked(e -> showTicketDetails(ticket));

        return card;
    }

    // ---------- FAQ (stub) ----------

    @FXML
    private void filterFaq() {
        String searchText = faqSearchField.getText().toLowerCase().trim();
        faqListContainer.getChildren().clear();

        // Hardcoded FAQ articles
        List<FAQArticle> articles = Arrays.asList(
                new FAQArticle(
                        "How to Reset Your Password",
                        "To reset your password: 1. Go to login page 2. Click \"Forgot Password\" 3. Enter your email 4. Check your email for reset link",
                        "Access",
                        "password reset forgot account login"
                ),
                new FAQArticle(
                        "VPN Connection Guide",
                        "To connect to VPN: 1. Download VPN client 2. Install and open 3. Enter credentials 4. Select server location 5. Click Connect",
                        "Network",
                        "vpn connection network remote access"
                ),
                new FAQArticle(
                        "Email Setup on Mobile Device",
                        "To setup email on mobile: 1. Open email app 2. Add account 3. Select Exchange 4. Enter company email and password 5. Complete setup",
                        "Email",
                        "email setup mobile device exchange account"
                )
        );

        // Filter by search text
        List<FAQArticle> filtered = articles.stream()
                .filter(article -> searchText.isEmpty() || article.matchesSearch(searchText))
                .collect(java.util.stream.Collectors.toList());

        // Display results
        if (filtered.isEmpty()) {
            Label noResults = new Label("No articles found. Try different keywords.");
            noResults.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
            faqListContainer.getChildren().add(noResults);
        } else {
            for (FAQArticle article : filtered) {
                VBox card = createFaqCard(article);
                faqListContainer.getChildren().add(card);
            }
        }
    }

    private VBox createFaqCard(FAQArticle article) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8;");
        card.setPrefWidth(Double.MAX_VALUE);

        // Icon + Title + Views (top row)
        HBox topRow = new HBox(10);
        topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        topRow.setPrefWidth(Double.MAX_VALUE);

        Label icon = new Label("📖");
        icon.setStyle("-fx-font-size: 16px;");

        Label title = new Label(article.title);
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");
        title.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label views = new Label("👁 " + (100 + (int)(Math.random() * 200)) + " views");
        views.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");

        topRow.getChildren().addAll(icon, title, spacer, views);

        // Description
        Label desc = new Label(article.description);
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        desc.setPrefWidth(600);

        // Keywords + Category badge (bottom row)
        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label keywords = new Label(article.keywords);
        keywords.setWrapText(true);
        keywords.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, javafx.scene.layout.Priority.ALWAYS);

        Label category = new Label(article.category);
        category.setStyle("-fx-background-color: #e5e7eb; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11px; -fx-text-fill: #374151; -fx-font-weight: bold;");

        bottomRow.getChildren().addAll(keywords, spacer2, category);

        card.getChildren().addAll(topRow, desc, bottomRow);
        return card;
    }

    // Helper class for FAQ articles
    private static class FAQArticle {
        String title;
        String description;
        String category;
        String keywords;

        FAQArticle(String title, String description, String category, String keywords) {
            this.title = title;
            this.description = description;
            this.category = category;
            this.keywords = keywords;
        }

        boolean matchesSearch(String searchText) {
            return title.toLowerCase().contains(searchText) ||
                    description.toLowerCase().contains(searchText) ||
                    keywords.toLowerCase().contains(searchText);
        }
    }

    // ---------- LOGOUT & CHATBOT ----------
    @FXML
    private void handleLogout(ActionEvent event) {
        userService.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (IOException e) {
            showError("Error loading login screen: " + e.getMessage());
        }
    }

    @FXML
    private void openChatbot(ActionEvent event) {
        if (chatbotContainer == null) {
            showError("Chatbot UI not loaded correctly.");
            return;
        }
        boolean visible = chatbotContainer.isVisible();
        chatbotContainer.setVisible(!visible);
        chatbotContainer.setManaged(!visible);
    }

    // ---------- UTIL ----------

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showTicketDetails(Ticket ticket) {
        detailsTitleLabel.setText(ticket.getTitle());
        detailsTicketIdLabel.setText("Ticket #" + ticket.getId());

        detailsStatusBadge.setText(ticket.getStatus().toLowerCase());
        detailsPriorityBadge.setText(
                ticket.getPriority() != null ? ticket.getPriority().toLowerCase() : "normal"
        );

        detailsDescriptionLabel.setText(ticket.getDescription());

        detailsCategoryLabel.setText(
                ticket.getCategory() != null ? ticket.getCategory() : "Not specified"
        );
        detailsCreatedLabel.setText(
                ticket.getCreatedDate() != null ? ticket.getCreatedDate() : "-"
        );
        detailsChannelLabel.setText("Web"); // static for now
        detailsUpdatedLabel.setText(
                ticket.getUpdatedDate() != null ? ticket.getUpdatedDate() : "-"
        );

        String assigned = ticket.getAssignedTo() != null ? ticket.getAssignedTo() : "Unassigned";
        detailsAssignedToLabel.setText(assigned);

        ticketDetailsOverlay.setVisible(true);
        ticketDetailsOverlay.setManaged(true);
    }

}
