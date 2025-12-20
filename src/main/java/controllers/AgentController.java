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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Ticket;
import models.User;
import services.TicketService;
import services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AgentController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private StackPane contentArea;
    @FXML private VBox dashboardContent;
    @FXML private VBox myTicketsContent;
    @FXML private VBox ticketQueueContent;
    @FXML private VBox ticketsListContainer;
    @FXML private VBox queueCardsContainer;
    @FXML private Label ticketCountLabel;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> priorityFilterCombo;

    // Modal components
    private VBox ticketDetailsModal;
    private Ticket selectedTicket;

    private final UserService userService = UserService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (welcomeLabel != null) {
            User current = userService.getCurrentUser();
            if (current != null && current.getUsername() != null && !current.getUsername().isEmpty()) {
                welcomeLabel.setText("Welcome, " + current.getUsername() + "!");
            } else if (current != null) {
                welcomeLabel.setText("Welcome, Agent User!");
            } else {
                welcomeLabel.setText("Welcome, Agent!");
            }
        }

        // Init filter dropdowns
        if (statusFilterCombo != null) {
            statusFilterCombo.getItems().setAll("All", "New", "In Progress", "Escalated", "Resolved");
            statusFilterCombo.getSelectionModel().select("All");
        }
        if (priorityFilterCombo != null) {
            priorityFilterCombo.getItems().setAll("All", "Low", "Medium", "High", "Urgent");
            priorityFilterCombo.getSelectionModel().select("All");
        }

        // Load BOTH my tickets AND unassigned queue on startup
        loadMyTickets();
        loadUnassignedTickets();
    }

    private void loadMyTickets() {
        if (ticketsListContainer == null) return;
        ticketsListContainer.getChildren().clear();

        User current = userService.getCurrentUser();
        if (current == null) {
            if (ticketCountLabel != null) ticketCountLabel.setText("0 total tickets");
            return;
        }

        String agentName = current.getUsername() != null ? current.getUsername() : current.getEmail();
        List<Ticket> allTickets = TicketService.getInstance().getAllTickets();

        int count = 0;
        for (Ticket ticket : allTickets) {
            if (ticket.getAssignedTo() != null && agentName.equalsIgnoreCase(ticket.getAssignedTo())) {
                VBox card = createTicketCard(ticket);
                ticketsListContainer.getChildren().add(card);
                count++;
            }
        }

        if (ticketCountLabel != null) {
            ticketCountLabel.setText(count + " assigned tickets");
        }

        if (count == 0) {
            Label none = new Label("No assigned tickets yet.");
            none.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
            ticketsListContainer.getChildren().add(none);
        }
    }

    private void loadUnassignedTickets() {
        if (queueCardsContainer == null) return;
        queueCardsContainer.getChildren().clear();

        List<Ticket> allTickets = TicketService.getInstance().getAllTickets();

        int count = 0;
        for (Ticket ticket : allTickets) {
            if (ticket.getAssignedTo() == null || ticket.getAssignedTo().isEmpty()) {
                VBox card = createTicketCard(ticket);
                queueCardsContainer.getChildren().add(card);
                count++;
            }
        }

        if (count == 0) {
            Label none = new Label("All tickets assigned.");
            none.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
            queueCardsContainer.getChildren().add(none);
        }
    }

    private VBox createTicketCard(Ticket ticket) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-cursor: hand;");

        Label title = new Label(ticket.getTitle());
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label student = new Label("From: " + ticket.getStudentEmail());
        student.setStyle("-fx-text-fill: #5a6c7d; -fx-font-size: 11px;");

        Label meta = new Label();
        String cat = ticket.getCategory() != null ? ticket.getCategory() : "-";
        String pri = ticket.getPriority() != null ? ticket.getPriority() : "-";
        String status = ticket.getStatus() != null ? ticket.getStatus() : "-";
        meta.setText("Status: " + status + "   Category: " + cat + "   Priority: " + pri);
        meta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

        Label desc = new Label(ticket.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");

        card.getChildren().addAll(title, student, meta, desc);

        // Make card clickable
        card.setOnMouseClicked(e -> showTicketDetails(ticket));

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10; -fx-border-color: #2196F3; -fx-border-radius: 10; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-cursor: hand;"));

        return card;
    }

    private void showTicketDetails(Ticket ticket) {
        selectedTicket = ticket;

        // Create modal overlay
        BorderPane modal = new BorderPane();
        modal.setStyle("-fx-background-color: rgba(0,0,0,0.45);");

        // Create details card
        VBox detailsCard = new VBox(15);
        detailsCard.setPadding(new Insets(20));
        detailsCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-max-width: 700;");

        // Header with close button
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label titleLabel = new Label(ticket.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Region spacer = new Region();
        spacer.setPrefWidth(Double.MAX_VALUE);
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> contentArea.getChildren().remove(modal));
        header.getChildren().addAll(titleLabel, spacer, closeBtn);

        // Ticket ID
        Label idLabel = new Label("Ticket #" + ticket.getId());
        idLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

        // Status and Priority badges
        HBox badges = new HBox(6);
        Label statusBadge = new Label(ticket.getStatus() != null ? ticket.getStatus() : "NEW");
        statusBadge.setStyle("-fx-background-color: #e0d9ff; -fx-text-fill: #4b3f72; -fx-padding: 3 8; -fx-background-radius: 999; -fx-font-size: 11px;");
        Label priorityBadge = new Label(ticket.getPriority() != null ? ticket.getPriority() : "MEDIUM");
        priorityBadge.setStyle("-fx-background-color: #ffe5b4; -fx-text-fill: #92400e; -fx-padding: 3 8; -fx-background-radius: 999; -fx-font-size: 11px;");
        badges.getChildren().addAll(statusBadge, priorityBadge);

        // Description
        Label descLabel = new Label("Description");
        descLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        Label descContent = new Label(ticket.getDescription());
        descContent.setWrapText(true);
        descContent.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");

        // Info grid
        HBox infoGrid = new HBox(40);
        VBox col1 = new VBox(4);
        Label catLabel = new Label("Category:");
        catLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");
        Label catValue = new Label(ticket.getCategory() != null ? ticket.getCategory() : "-");
        catValue.setStyle("-fx-font-size: 12px;");
        Label createdLabel = new Label("Created:");
        createdLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");
        Label createdValue = new Label("-");
        createdValue.setStyle("-fx-font-size: 12px;");
        col1.getChildren().addAll(catLabel, catValue, createdLabel, createdValue);

        VBox col2 = new VBox(4);
        Label channelLabel = new Label("Channel:");
        channelLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");
        Label channelValue = new Label("Web");
        channelValue.setStyle("-fx-font-size: 12px;");
        Label updatedLabel = new Label("Last Updated:");
        updatedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");
        Label updatedValue = new Label("-");
        updatedValue.setStyle("-fx-font-size: 12px;");
        col2.getChildren().addAll(channelLabel, channelValue, updatedLabel, updatedValue);

        infoGrid.getChildren().addAll(col1, col2);

        // Assigned to
        VBox assignedBox = new VBox(2);
        Label assignedLabel = new Label("Assigned To");
        assignedLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        Label assignedValue = new Label(ticket.getAssignedTo() != null ? ticket.getAssignedTo() : "Unassigned");
        assignedValue.setStyle("-fx-font-size: 12px;");
        assignedBox.getChildren().addAll(assignedLabel, assignedValue);

        // ===== UPDATE TICKET SECTION =====
        Label updateSectionLabel = new Label("Update Ticket");
        updateSectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Status dropdown
        VBox statusBox = new VBox(5);
        Label statusLabel = new Label("Status");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().setAll("New", "In Progress", "Resolved", "Escalated");
        statusCombo.setValue(ticket.getStatus() != null ? ticket.getStatus() : "New");
        statusCombo.setStyle("-fx-padding: 8;");
        statusBox.getChildren().addAll(statusLabel, statusCombo);

        // Resolution notes
        VBox notesBox = new VBox(5);
        Label notesLabel = new Label("Resolution Notes");
        notesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        TextArea notesField = new TextArea();
        notesField.setPrefHeight(100);
        notesField.setWrapText(true);
        notesField.setPromptText("Add resolution details...");
        notesBox.getChildren().addAll(notesLabel, notesField);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-padding: 10 30; -fx-font-size: 13px; -fx-background-radius: 6; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> saveTicketChanges(statusCombo.getValue(), notesField.getText(), modal));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #d0d0d0; -fx-padding: 10 30; -fx-font-size: 13px; -fx-background-radius: 6; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> contentArea.getChildren().remove(modal));

        buttonBox.getChildren().addAll(saveBtn, cancelBtn);

        // Add all to details card
        detailsCard.getChildren().addAll(
                header,
                idLabel,
                badges,
                descLabel,
                descContent,
                infoGrid,
                assignedBox,
                new Separator(),
                updateSectionLabel,
                statusBox,
                notesBox,
                buttonBox
        );

        // Center the card in the modal
        BorderPane.setAlignment(detailsCard, javafx.geometry.Pos.CENTER);
        modal.setCenter(detailsCard);

        // Add modal to content area
        contentArea.getChildren().add(modal);
    }

    private void saveTicketChanges(String newStatus, String resolutionNotes, BorderPane modal) {
        User current = userService.getCurrentUser();
        if (current == null || selectedTicket == null) return;

        selectedTicket.setStatus(newStatus);
        selectedTicket.setAssignedTo(current.getEmail());
        if (resolutionNotes != null && !resolutionNotes.isEmpty()) {
            selectedTicket.setResolutionNotes(resolutionNotes);
        }
        selectedTicket.setUpdatedDate(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")));

        // ALWAYS use updateTicket for existing tickets
        TicketService.getInstance().updateTicket(selectedTicket);

        // Force reload everything from file
        TicketService.getInstance().reloadAllTickets();

        // Small delay to ensure file is written
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Refresh displays
        loadMyTickets();
        loadUnassignedTickets();

        contentArea.getChildren().remove(modal);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Ticket Assigned & Updated");
        alert.setContentText("Ticket assigned to you with status: " + newStatus);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        userService.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
